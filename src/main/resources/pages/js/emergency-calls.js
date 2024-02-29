import { initTable, setHandler } from "./init-table.js";
import { loadSettings, statusCodeActions } from "./load-settings.js";
import { setColor } from "./util.js";

const emergencyCalls = initTable(
    [{}, {text: "Location", colspan: 3}],
    [
        {text: 'Call Number', order: 'callNumber', search: 'callNumber'},
        {text: 'X', order: 'locationX', search: 'locationX'},
        {text: 'Y', order: 'locationY', search: 'locationY'},
        {text: 'Z', order: 'locationZ', search: 'locationZ'},
        {text: 'Created At', order: 'createdAt', search: 'createdAt'},
        {text: 'Caller', order: 'caller', search: 'caller'},
        {text: 'Closed', order: 'closed', search: 'closed'}
    ]
);

let settings;

loadSettings((sett) => {
    settings = sett;
    setHandler(handler);
}, true);

function handler(page, order, handleHeader, handleFooter) {
    $.ajax({
        url: '/api/emergency-calls',
        method: 'GET',
        data: {
            page: page,
            order: order
        },
        success: (res) => {
            emergencyCalls.innerHTML = '';
            for(const emergencyCall of res.emergencyCalls) {
                const tr = document.createElement('tr');
                const callNumber = emergencyCall.callNumber;
                const callNumberTd = document.createElement('td');
                callNumberTd.innerText = callNumber;
                const locationXTd = document.createElement('td');
                locationXTd.innerText = emergencyCall.locationX;
                const locationYTd = document.createElement('td');
                locationYTd.innerText = emergencyCall.locationY;
                const locationZTd = document.createElement('td');
                locationZTd.innerText = emergencyCall.locationZ;
                const createdAtTd = document.createElement('td');
                createdAtTd.innerText = emergencyCall.createdAt;
                let caller = emergencyCall.caller;
                const callerTd = document.createElement('td');
                if(caller == null) {
                    caller = 'null';
                    callerTd.classList.add('null');
                }
                callerTd.innerText = caller;
                const closed = emergencyCall.closed;
                const closedTd = document.createElement('td');
                closedTd.innerText = closed;
                closedTd.classList.add(closed ? 'true' : 'false');
                tr.appendChild(callNumberTd);
                tr.appendChild(locationXTd);
                tr.appendChild(locationYTd);
                tr.appendChild(locationZTd);
                tr.appendChild(createdAtTd);
                tr.appendChild(callerTd);
                tr.appendChild(closedTd);
                emergencyCalls.appendChild(tr);
            }
            handleHeader(order);
            handleFooter(page, res.pages);
        },
        statusCode: statusCodeActions
    });
}