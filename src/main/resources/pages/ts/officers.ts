import { loadSettings } from "./load-settings.js";
import { BooleanTableData, EmptyTableHeaderGroup, Extra, LinkTableHeader, PrimaryKeyTableHeader, StringTableData, Table, TableData, TableHeader, TableHeaderGroup, TableRow } from "./table.js";

await loadSettings();

type Officer = {
    uuid: string;
    username: string;
    online: boolean;
    status: string;
    statusColor: number;
    rank: string;
    rankColor: number;
    callsign: string | null;
    callsignReserved: boolean;
};

class OfficersTable extends Table {
    public constructor() {
        super('/api/officers', 'officers', [
            new EmptyTableHeaderGroup(),
            new EmptyTableHeaderGroup(),
            new EmptyTableHeaderGroup(),
            new EmptyTableHeaderGroup(),
            new EmptyTableHeaderGroup(),
            new TableHeaderGroup('Callsign', 2)
        ], [
            new PrimaryKeyTableHeader('UUID', 'uuid'),
            new TableHeader('Username', 'username'),
            new TableHeader('Online', 'online'),
            new TableHeader('Status', 'status'),
            new TableHeader('Rank', 'rank'),
            new TableHeader('Callsign', 'callsign'),
            new TableHeader('Reserved', 'callsignReserved'),
            new LinkTableHeader('View')
        ]);
    }

    public parseElement(element: Officer): TableRow {
        return new OfficersTableRow(element);
    }
}

class CallsignTableData extends StringTableData {
    public appendTo(row: TableRow): void {
        const td = document.createElement('td');
        td.innerText = this.value ?? 'null';
        td.classList.add(this.value == null ? 'null' : 'callsign');
        td.style.color = '#' + this.color?.toString(16).padStart(6, '0');
        row.appendChild(td);
    }
}

class OfficersTableRow extends TableRow {
    public parseData(element: Officer): TableData<any>[] {
        return [
            new StringTableData(element.uuid, undefined, true),
            new StringTableData(element.username),
            new BooleanTableData(element.online),
            new StringTableData(element.status, element.statusColor),
            new StringTableData(element.rank, element.rankColor),
            new CallsignTableData(element.callsign),
            new BooleanTableData(element.callsignReserved)
        ];
    }
}

const officersTable = new OfficersTable();