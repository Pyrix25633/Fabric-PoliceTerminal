import { loadSettings } from "./load-settings.js";
import { BooleanTableData, Extra, LinkTableHeader, PrimaryKeyTableHeader, StringTableData, Table, TableData, TableHeader, TableRow } from "./table.js";

await loadSettings();

type Citizen = {
    uuid: string;
    username: string;
    online: boolean;
};

class CitizensTable extends Table {
    public constructor() {
        super('/api/citizens', 'citizens', null, [
            new PrimaryKeyTableHeader('UUID', 'uuid'),
            new TableHeader('Username', 'username'),
            new TableHeader('Online', 'online'),
            new LinkTableHeader('View')
        ]);
    }

    public parseElement(element: Citizen): TableRow {
        return new CitizensTableRow(element);
    }
}

class CitizensTableRow extends TableRow {
    public parseData(element: Citizen): TableData<any>[] {
        return [
            new StringTableData(element.uuid, undefined, true),
            new StringTableData(element.username),
            new BooleanTableData(element.online)
        ];
    }
}

const citizensTable = new CitizensTable();