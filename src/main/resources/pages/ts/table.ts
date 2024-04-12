import { RequireNonNull, defaultStatusCode } from "./utils.js";

type Order = {
    column: string;
    ascending: boolean;
}

export abstract class Table {
    private readonly url: string;
    private readonly resourceName: string;
    private readonly table: HTMLTableElement;
    private readonly groupsRow: HTMLTableRowElement | null;
    private readonly headersRow: HTMLTableRowElement;
    private readonly groups: TableHeaderGroup[] | null;
    private readonly headers: TableHeader[];
    private order: Order;
    private readonly body: HTMLTableSectionElement;
    private readonly footer: TableFooter;
    private page: number;

    public constructor(url: string, resourceName: string, groups: TableHeaderGroup[] | null, headers: TableHeader[]) {
        this.url = url;
        this.resourceName = resourceName;

        this.table = RequireNonNull.getElementById('table') as HTMLTableElement;
        const head = document.createElement('thead');
        this.table.appendChild(head);
        if(groups !== null) {
            this.groupsRow = document.createElement('tr');
            head.appendChild(this.groupsRow);
        }
        else
            this.groupsRow = null;
        this.headersRow = document.createElement('tr');
        head.appendChild(this.headersRow);
        this.headers = headers;
        this.order = { column: headers[0].column, ascending: true };
        for(const header of headers)
            header.appendTo(this);
        this.groups = groups;
        for(const group of groups ?? [])
            group.appendTo(this);
        this.body = document.createElement('tbody');
        this.table.appendChild(this.body);

        this.page = 0;
        this.footer = new TableFooter(this);
        this.footer.appendTo(this);

        this.update();
    }

    public appendChild(node: HTMLTableSectionElement): void {
        this.table.appendChild(node);
    }

    public appendChildToGroups(node: HTMLTableCellElement): void {
        this.groupsRow?.appendChild(node);
    }

    public appendChildToHeaders(node: HTMLTableCellElement): void {
        this.headersRow.appendChild(node);
    }

    public appendChildToBody(node: HTMLTableRowElement): void {
        this.body.appendChild(node);
    }

    public getOrder(): Order {
        return this.order;
    }

    public setOrder(order: Order): void {
        this.order = order;
        for(const header of this.headers)
            header.updateOrderImg(order);
        this.update();
    }

    public getPage(): number {
        return this.page;
    }

    public setPage(page: number): void {
        this.page = page;
        this.update();
    }

    public update(): void {
        $.ajax({
            url: this.url,
            method: 'GET',
            data: { page: this.page, order: this.order },
            contentType: 'application/json',
            success: (res: { pages: number; [index: string]: any; }): void => {
                this.footer.update(new PageHelper(this.page, res.pages));
                this.body.innerHTML = '';
                for(const element of res[this.resourceName] as Element[]) {
                    const row = this.parseElement(element);
                    row.appendTo(this);
                }
                // Fix for weird FireFox rendering bug
                $('#table').hide();
                setTimeout((): void => {
                    $('#table').show();
                }, 250);
            },
            statusCode: defaultStatusCode
        });
    }

    public abstract parseElement(element: Element): TableRow;
}

class GenericTableHeader {
    protected readonly text: string;
    
    public constructor(text: string) {
        this.text = text;
    }
}

export enum Extra {
    PrimaryKey,
    Link
}

export class TableHeader extends GenericTableHeader {
    public readonly column: string;
    private readonly orderImg: HTMLImageElement;
    private readonly extra: Extra | undefined;
    
    public constructor(text: string, column: string, extra: Extra | undefined = undefined) {
        super(text);
        this.column = column;
        this.orderImg = document.createElement('img');
        this.orderImg.classList.add('button');
        this.orderImg.alt = 'Order Icon';
        this.extra = extra;
    }

    public appendTo(table: Table): void {
        const th = document.createElement('th');
        const div = document.createElement('div');
        div.classList.add('container');
        const span = document.createElement('span');
        span.classList.add('th');
        if(this.extra == Extra.PrimaryKey)
            span.classList.add('primary-key');
        else if(this.extra == Extra.Link)
            span.classList.add('link');
        span.innerText = this.text;
        if(this.extra != Extra.Link) {
            this.updateOrderImg(table.getOrder());
            this.orderImg.addEventListener('click', (): void => {
                let order = table.getOrder();
                if(order.column == this.column)
                    order = { column: order.column, ascending: !order.ascending };
                else
                    order = { column: this.column, ascending: true };
                table.setOrder(order);
            });
        }
        div.appendChild(span);
        if(this.extra != Extra.Link)
            div.appendChild(this.orderImg);
        th.appendChild(div);
        table.appendChildToHeaders(th);
    }

    public updateOrderImg(order: Order): void {
        this.orderImg.src = '/img/order' + (order.column == this.column ? '-' + (order.ascending ? 'ascending' : 'descending') : '') + '.svg';
    }
}

export class PrimaryKeyTableHeader extends TableHeader {
    public constructor(text: string, column: string) {
        super(text, column, Extra.PrimaryKey);
    }
}

export class LinkTableHeader extends TableHeader {
    public constructor(text: string) {
        super(text, '', Extra.Link);
    }
}

export class TableHeaderGroup extends GenericTableHeader {
    private readonly colspan: number;

    public constructor(text: string, colspan: number) {
        super(text);
        this.colspan = colspan;
    }

    public appendTo(table: Table): void {
        const th = document.createElement('th');
        th.innerText = this.text;
        th.classList.add('group');
        th.colSpan = this.colspan;
        table.appendChildToGroups(th);
    }
}

export class EmptyTableHeaderGroup extends TableHeaderGroup {
    public constructor() {
        super('', 0);
    }

    public appendTo(table: Table): void {
        const th = document.createElement('th');
        table.appendChildToGroups(th);
    }
}

type Element = { [index: string]: any; };

export abstract class TableData<T> {
    protected readonly value: T | null;
    protected readonly color: number | undefined;
    protected readonly primaryKey: boolean;

    public constructor(value: T | null, color: number | undefined = undefined, primaryKey: boolean = false) {
        this.value = value;
        this.color = color;
        this.primaryKey = primaryKey;
    }

    public createTd(): HTMLTableCellElement {
        const td = document.createElement('td');
        td.innerText = this.value != null ? this.value.toString() : 'null';
        if(this.value == null)
            td.classList.add('null');
        td.style.color = '#' + this.color?.toString(16).padStart(6, '0');
        if(this.primaryKey)
            td.classList.add('primary-key');
        return td;
    }

    public appendTo(row: TableRow): void {
        row.appendChild(this.createTd());
    };
}

export class StringTableData extends TableData<string> {}

export class BooleanTableData extends TableData<boolean> {
    public createTd(): HTMLTableCellElement {
        const td = super.createTd();
        if(this.value != null)
            td.classList.add(this.value.toString());
        return td;
    }
}

export class NumberTableData extends TableData<number> {}

export class LinkTableData extends TableData<string> {
    private readonly href: string;

    constructor(value: string, href: string) {
        super(value);
        this.href = href;
    }

    public createTd(): HTMLTableCellElement {
        const td = document.createElement('td');
        const a = document.createElement('a');
        a.innerText = this.value ?? '';
        a.href = this.href;
        td.appendChild(a);
        return td;
    }
}

export abstract class TableRow {
    private readonly tableData: TableData<any>[];
    private readonly row: HTMLTableRowElement;

    public constructor(element: Element) {
        this.tableData = this.parseData(element);
        this.row = document.createElement('tr');
        for(const data of this.tableData)
            data.appendTo(this);
    }

    public abstract parseData(element: Element): TableData<any>[];

    public appendChild(node: HTMLTableCellElement): void {
        this.row.appendChild(node);
    }

    public appendTo(table: Table): void {
        table.appendChildToBody(this.row);
    }
}

export class PageHelper {
    public readonly first: number;
    public readonly previous: number;
    public readonly current: number;
    public readonly next: number;
    public readonly last: number;
    public readonly total: number;

    public constructor(current: number, total: number) {
        this.first = 0;
        this.previous = current - 1;
        if(this.previous < 0) this.previous = 0;
        this.current = current;
        this.last = total - 1;
        if(this.last < 0) this.last = 0;
        this.next = current + 1;
        if(this.next > this.last) this.next = this.last;
        this.total = total;
    }
}

export class TableFooter {
    private readonly first: HTMLImageElement;
    private readonly previous: HTMLImageElement;
    private readonly current: HTMLInputElement;
    private currentInputTimeout: number | undefined = undefined;
    private readonly total: HTMLSpanElement;
    private readonly next: HTMLImageElement;
    private readonly last: HTMLImageElement;
    private pageHelper: PageHelper;

    public constructor(table: Table) {
        this.first = TableFooter.createImage('First', 'first');
        this.first.addEventListener('click', (): void => {
            table.setPage(this.pageHelper.first);
        });

        this.previous = TableFooter.createImage('Previous', 'previous');
        this.previous.addEventListener('click', (): void => {
            table.setPage(this.pageHelper.previous);
        });

        this.current = document.createElement('input');
        this.current.type = 'number';
        this.current.id = 'page';
        const currentInputHandler = (): void => {
            table.setPage(parseInt(this.current.value));
        };
        this.current.addEventListener('keyup', () => {
            clearTimeout(this.currentInputTimeout);
            this.currentInputTimeout = setTimeout(currentInputHandler, 1000);
        });
        this.current.addEventListener('keydown', () => {
            clearTimeout(this.currentInputTimeout);
        });
        this.current.addEventListener('focusout', () => {
            clearTimeout(this.currentInputTimeout);
            currentInputHandler();
        });

        this.total = document.createElement('span');

        this.next = TableFooter.createImage('Next', 'next');
        this.next.addEventListener('click', (): void => {
            table.setPage(this.pageHelper.next);
        });

        this.last = TableFooter.createImage('Last', 'last');
        this.last.addEventListener('click', (): void => {
            table.setPage(this.pageHelper.last);
        });

        this.pageHelper = new PageHelper(table.getPage(), 0);
        this.update(this.pageHelper);
    }

    public static createImage(name: string, id: string): HTMLImageElement {
        const img = document.createElement('img');
        img.classList.add('button');
        img.alt = name + ' Icon';
        img.src = '/img/page-' + id + '.svg';
        return img;
    }

    public appendTo(table: Table): void {
        const tfoot = document.createElement('tfoot');
        const tr = document.createElement('tr');
        const td = document.createElement('td');
        td.colSpan = 100;
        const div = document.createElement('div');
        div.classList.add('container');
        div.appendChild(this.first);
        div.appendChild(this.previous);
        const label = document.createElement('label');
        label.htmlFor = 'page';
        label.innerText = 'Page';
        div.appendChild(label);
        div.appendChild(this.current);
        const slash = document.createTextNode('/');
        div.appendChild(slash);
        div.appendChild(this.total);
        div.appendChild(this.next);
        div.appendChild(this.last);
        td.appendChild(div);
        tr.appendChild(td);
        tfoot.appendChild(tr);
        table.appendChild(tfoot);
    }

    public update(pageHelper: PageHelper): void {
        this.pageHelper = pageHelper;
        this.current.value = (this.pageHelper.current + 1).toString();
        this.total.innerText = this.pageHelper.total.toString();
    }
}