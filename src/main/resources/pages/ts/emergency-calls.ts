import { loadSettings } from "./load-settings.js";
import { EmptyTableHeaderGroup, Extra, LinkTableHeader, NumberTableData, PrimaryKeyTableHeader, StringTableData, Table, TableData, TableHeader, TableHeaderGroup, TableRow } from "./table.js";

await loadSettings();

type EmercencyCall = {
    callNumber: number;
    locationX: number;
    locationY: number;
    locationZ: number;
    createdAt: string;
    caller: string;
    responder: string;
    closedAt: string;
};

class EmergencyCallsTable extends Table {
    public constructor() {
        super('/api/emergency-calls', 'emergencyCalls', [
            new EmptyTableHeaderGroup(),
            new TableHeaderGroup('Location', 3),
            new EmptyTableHeaderGroup(),
            new EmptyTableHeaderGroup(),
            new EmptyTableHeaderGroup(),
            new EmptyTableHeaderGroup()
        ], [
            new PrimaryKeyTableHeader('Call Number', 'callNumber'),
            new TableHeader('X', 'locationX'),
            new TableHeader('Y', 'locationY'),
            new TableHeader('Z', 'locationZ'),
            new TableHeader('Created At', 'createdAt'),
            new TableHeader('Caller', 'caller'),
            new TableHeader('Responder', 'responder'),
            new TableHeader('Closed At', 'closedAt'),
            new LinkTableHeader('View')
        ]);
    }

    public parseElement(element: EmercencyCall): TableRow {
        return new OfficersTableRow(element);
    }
}

class OfficersTableRow extends TableRow {
    public parseData(element: EmercencyCall): TableData<any>[] {
        return [
            new NumberTableData(element.callNumber, undefined, true),
            new NumberTableData(element.locationX),
            new NumberTableData(element.locationY),
            new NumberTableData(element.locationZ),
            new StringTableData(element.createdAt),
            new StringTableData(element.caller),
            new StringTableData(element.responder),
            new StringTableData(element.closedAt)
        ];
    }
}

const officersTable = new EmergencyCallsTable();