import { loadSettings } from "./load-settings.js";
import { BooleanTableData, EmptyTableHeaderGroup, StringTableData, Table, TableData, TableHeader, TableHeaderGroup, TableRow, UuidTableData } from "./table.js";

await loadSettings();

type Officer = {
    uuid: string;
    username: string;
    online: boolean;
    status: string;
    statusColor: number;
    rank: string;
    rankColor: number;
    callsign: string;
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
            new TableHeaderGroup('Callsing', 2)
        ], [
            new TableHeader('UUID', 'uuid', true),
            new TableHeader('Username', 'username'),
            new TableHeader('Online', 'online'),
            new TableHeader('Status', 'status'),
            new TableHeader('Rank', 'rank'),
            new TableHeader('Callsign', 'callsign'),
            new TableHeader('Reserved', 'callsignReserved')
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
            new UuidTableData(element.uuid),
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