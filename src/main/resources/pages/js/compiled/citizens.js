import { loadSettings } from "./load-settings.js";
import { BooleanTableData, LinkTableHeader, PrimaryKeyTableHeader, StringTableData, Table, TableHeader, TableRow } from "./table.js";
await loadSettings();
class CitizensTable extends Table {
    constructor() {
        super('/api/citizens', 'citizens', null, [
            new PrimaryKeyTableHeader('UUID', 'uuid'),
            new TableHeader('Username', 'username'),
            new TableHeader('Online', 'online'),
            new LinkTableHeader('View')
        ]);
    }
    parseElement(element) {
        return new CitizensTableRow(element);
    }
}
class CitizensTableRow extends TableRow {
    parseData(element) {
        return [
            new StringTableData(element.uuid, undefined, true),
            new StringTableData(element.username),
            new BooleanTableData(element.online)
        ];
    }
}
const citizensTable = new CitizensTable();
