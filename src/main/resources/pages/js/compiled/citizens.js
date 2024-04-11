import { loadSettings } from "./load-settings.js";
import { BooleanTableData, StringTableData, Table, TableHeader, TableRow, UuidTableData } from "./table.js";
await loadSettings();
class CitizensTable extends Table {
    constructor() {
        super('/api/citizens', 'citizens', null, [
            new TableHeader('UUID', 'uuid', true),
            new TableHeader('Username', 'username'),
            new TableHeader('Online', 'online')
        ]);
    }
    parseElement(element) {
        return new CitizensTableRow(element);
    }
}
class CitizensTableRow extends TableRow {
    parseData(element) {
        return [new UuidTableData(element.uuid), new StringTableData(element.username), new BooleanTableData(element.online)];
    }
}
const citizensTable = new CitizensTable();
