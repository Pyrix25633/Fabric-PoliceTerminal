import { loadSettings } from "./load-settings.js";
import { BooleanTableData, EmptyTableHeaderGroup, StringTableData, Table, TableHeader, TableHeaderGroup, TableRow, UuidTableData } from "./table.js";
await loadSettings();
class OfficersTable extends Table {
    constructor() {
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
