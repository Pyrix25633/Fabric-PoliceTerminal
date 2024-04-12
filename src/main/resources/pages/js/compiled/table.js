import { RequireNonNull, defaultStatusCode } from "./utils.js";
export class Table {
    constructor(url, resourceName, groups, headers) {
        this.url = url;
        this.resourceName = resourceName;
        this.table = RequireNonNull.getElementById('table');
        const head = document.createElement('thead');
        this.table.appendChild(head);
        if (groups !== null) {
            this.groupsRow = document.createElement('tr');
            head.appendChild(this.groupsRow);
        }
        else
            this.groupsRow = null;
        this.headersRow = document.createElement('tr');
        head.appendChild(this.headersRow);
        this.headers = headers;
        this.order = { column: headers[0].column, ascending: true };
        for (const header of headers)
            header.appendTo(this);
        this.groups = groups;
        for (const group of groups !== null && groups !== void 0 ? groups : [])
            group.appendTo(this);
        this.body = document.createElement('tbody');
        this.table.appendChild(this.body);
        this.page = 0;
        this.footer = new TableFooter(this);
        this.footer.appendTo(this);
        this.update();
    }
    appendChild(node) {
        this.table.appendChild(node);
    }
    appendChildToGroups(node) {
        var _a;
        (_a = this.groupsRow) === null || _a === void 0 ? void 0 : _a.appendChild(node);
    }
    appendChildToHeaders(node) {
        this.headersRow.appendChild(node);
    }
    appendChildToBody(node) {
        this.body.appendChild(node);
    }
    getOrder() {
        return this.order;
    }
    setOrder(order) {
        this.order = order;
        for (const header of this.headers)
            header.updateOrderImg(order);
        this.update();
    }
    getPage() {
        return this.page;
    }
    setPage(page) {
        this.page = page;
        this.update();
    }
    update() {
        $.ajax({
            url: this.url,
            method: 'GET',
            data: { page: this.page, order: this.order },
            contentType: 'application/json',
            success: (res) => {
                this.footer.update(new PageHelper(this.page, res.pages));
                this.body.innerHTML = '';
                for (const element of res[this.resourceName]) {
                    const row = this.parseElement(element);
                    row.appendTo(this);
                }
                // Fix for weird FireFox rendering bug
                $('#table').hide();
                setTimeout(() => {
                    $('#table').show();
                }, 250);
            },
            statusCode: defaultStatusCode
        });
    }
}
class GenericTableHeader {
    constructor(text) {
        this.text = text;
    }
}
export var Extra;
(function (Extra) {
    Extra[Extra["PrimaryKey"] = 0] = "PrimaryKey";
    Extra[Extra["Link"] = 1] = "Link";
})(Extra || (Extra = {}));
export class TableHeader extends GenericTableHeader {
    constructor(text, column, extra = undefined) {
        super(text);
        this.column = column;
        this.orderImg = document.createElement('img');
        this.orderImg.classList.add('button');
        this.orderImg.alt = 'Order Icon';
        this.extra = extra;
    }
    appendTo(table) {
        const th = document.createElement('th');
        const div = document.createElement('div');
        div.classList.add('container');
        const span = document.createElement('span');
        span.classList.add('th');
        if (this.extra == Extra.PrimaryKey)
            span.classList.add('primary-key');
        else if (this.extra == Extra.Link)
            span.classList.add('link');
        span.innerText = this.text;
        if (this.extra != Extra.Link) {
            this.updateOrderImg(table.getOrder());
            this.orderImg.addEventListener('click', () => {
                let order = table.getOrder();
                if (order.column == this.column)
                    order = { column: order.column, ascending: !order.ascending };
                else
                    order = { column: this.column, ascending: true };
                table.setOrder(order);
            });
        }
        div.appendChild(span);
        if (this.extra != Extra.Link)
            div.appendChild(this.orderImg);
        th.appendChild(div);
        table.appendChildToHeaders(th);
    }
    updateOrderImg(order) {
        this.orderImg.src = '/img/order' + (order.column == this.column ? '-' + (order.ascending ? 'ascending' : 'descending') : '') + '.svg';
    }
}
export class PrimaryKeyTableHeader extends TableHeader {
    constructor(text, column) {
        super(text, column, Extra.PrimaryKey);
    }
}
export class LinkTableHeader extends TableHeader {
    constructor(text) {
        super(text, '', Extra.Link);
    }
}
export class TableHeaderGroup extends GenericTableHeader {
    constructor(text, colspan) {
        super(text);
        this.colspan = colspan;
    }
    appendTo(table) {
        const th = document.createElement('th');
        th.innerText = this.text;
        th.classList.add('group');
        th.colSpan = this.colspan;
        table.appendChildToGroups(th);
    }
}
export class EmptyTableHeaderGroup extends TableHeaderGroup {
    constructor() {
        super('', 0);
    }
    appendTo(table) {
        const th = document.createElement('th');
        table.appendChildToGroups(th);
    }
}
export class TableData {
    constructor(value, color = undefined, primaryKey = false) {
        this.value = value;
        this.color = color;
        this.primaryKey = primaryKey;
    }
    createTd() {
        var _a;
        const td = document.createElement('td');
        td.innerText = this.value != null ? this.value.toString() : 'null';
        if (this.value == null)
            td.classList.add('null');
        td.style.color = '#' + ((_a = this.color) === null || _a === void 0 ? void 0 : _a.toString(16).padStart(6, '0'));
        if (this.primaryKey)
            td.classList.add('primary-key');
        return td;
    }
    appendTo(row) {
        row.appendChild(this.createTd());
    }
    ;
}
export class StringTableData extends TableData {
}
export class BooleanTableData extends TableData {
    createTd() {
        const td = super.createTd();
        if (this.value != null)
            td.classList.add(this.value.toString());
        return td;
    }
}
export class NumberTableData extends TableData {
}
export class LinkTableData extends TableData {
    constructor(value, href) {
        super(value);
        this.href = href;
    }
    createTd() {
        var _a;
        const td = document.createElement('td');
        const a = document.createElement('a');
        a.innerText = (_a = this.value) !== null && _a !== void 0 ? _a : '';
        a.href = this.href;
        td.appendChild(a);
        return td;
    }
}
export class TableRow {
    constructor(element) {
        this.tableData = this.parseData(element);
        this.row = document.createElement('tr');
        for (const data of this.tableData)
            data.appendTo(this);
    }
    appendChild(node) {
        this.row.appendChild(node);
    }
    appendTo(table) {
        table.appendChildToBody(this.row);
    }
}
export class PageHelper {
    constructor(current, total) {
        this.first = 0;
        this.previous = current - 1;
        if (this.previous < 0)
            this.previous = 0;
        this.current = current;
        this.last = total - 1;
        if (this.last < 0)
            this.last = 0;
        this.next = current + 1;
        if (this.next > this.last)
            this.next = this.last;
        this.total = total;
    }
}
export class TableFooter {
    constructor(table) {
        this.currentInputTimeout = undefined;
        this.first = TableFooter.createImage('First', 'first');
        this.first.addEventListener('click', () => {
            table.setPage(this.pageHelper.first);
        });
        this.previous = TableFooter.createImage('Previous', 'previous');
        this.previous.addEventListener('click', () => {
            table.setPage(this.pageHelper.previous);
        });
        this.current = document.createElement('input');
        this.current.type = 'number';
        this.current.id = 'page';
        const currentInputHandler = () => {
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
        this.next.addEventListener('click', () => {
            table.setPage(this.pageHelper.next);
        });
        this.last = TableFooter.createImage('Last', 'last');
        this.last.addEventListener('click', () => {
            table.setPage(this.pageHelper.last);
        });
        this.pageHelper = new PageHelper(table.getPage(), 0);
        this.update(this.pageHelper);
    }
    static createImage(name, id) {
        const img = document.createElement('img');
        img.classList.add('button');
        img.alt = name + ' Icon';
        img.src = '/img/page-' + id + '.svg';
        return img;
    }
    appendTo(table) {
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
    update(pageHelper) {
        this.pageHelper = pageHelper;
        this.current.value = (this.pageHelper.current + 1).toString();
        this.total.innerText = this.pageHelper.total.toString();
    }
}
