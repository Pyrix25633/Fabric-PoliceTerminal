import { loadSettings } from "./load-settings.js";
import { BooleanTableData, EmptyTableHeaderGroup, LinkTableHeader, PrimaryKeyTableHeader, StringTableData, Table, TableHeader, TableHeaderGroup, TableRow } from "./table.js";
await loadSettings();
class OfficersTable extends Table {
    constructor() {
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
    parseElement(element) {
        return new OfficersTableRow(element);
    }
}
class CallsignTableData extends StringTableData {
    appendTo(row) {
        var _a, _b;
        const td = document.createElement('td');
        td.innerText = (_a = this.value) !== null && _a !== void 0 ? _a : 'null';
        td.classList.add(this.value == null ? 'null' : 'callsign');
        td.style.color = '#' + ((_b = this.color) === null || _b === void 0 ? void 0 : _b.toString(16).padStart(6, '0'));
        row.appendChild(td);
    }
}
class OfficersTableRow extends TableRow {
    parseData(element) {
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
