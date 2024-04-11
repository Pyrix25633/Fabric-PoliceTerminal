import { loadSettings } from "./load-settings.js";
import { BooleanTableData, StringTableData, Table, TableData, TableHeader, TableRow, UuidTableData } from "./table.js";

await loadSettings();

type Citizen = {
    uuid: string;
    username: string;
    online: boolean;
};

class CitizensTable extends Table {
    public constructor() {
        super('/api/citizens', 'citizens', null, [
            new TableHeader('UUID', 'uuid', true),
            new TableHeader('Username', 'username'),
            new TableHeader('Online', 'online')
        ]);
    }

    public parseElement(element: Citizen): TableRow {
        return new CitizensTableRow(element);
    }
}

class CitizensTableRow extends TableRow {
    public parseData(element: Citizen): TableData<any>[] {
        return [new UuidTableData(element.uuid), new StringTableData(element.username), new BooleanTableData(element.online)];
    }
}

const citizensTable = new CitizensTable();