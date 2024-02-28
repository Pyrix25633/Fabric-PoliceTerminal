import { initTable, setHandler } from "./init-table.js";
import { loadSettings, statusCodeActions } from "./load-settings.js";
import { setColor } from "./util.js";

const officers = initTable(
    [{}, {}, {}, {}, {}, {text: 'Callsign', colspan: 2}],
    [
        {text: 'UUID', order: 'uuid', search: 'uuid'},
        {text: 'Username', order: 'username', search: 'username'},
        {text: 'Online', order: 'online', search: 'online'},
        {text: 'Status', order: 'status', search: 'status'},
        {text: 'Rank', order: 'rankId', search: 'rank'},
        {text: 'Callsign', order: 'callsign', search: 'callsign'},
        {text: 'Reserved', order: 'callsignReserved', search: 'callsignReserved'}
    ]
);

let settings;

loadSettings((sett) => {
    settings = sett;
    setHandler(handler);
}, true);

function handler(page, order, handleHeader, handleFooter) {
    $.ajax({
        url: '/api/officers',
        method: 'GET',
        data: {
            page: page,
            order: order
        },
        success: (res) => {
            officers.innerHTML = '';
            for(const officer of res.officers) {
                const tr = document.createElement('tr');
                const uuidTd = document.createElement('td');
                uuidTd.innerText = officer.uuid;
                uuidTd.classList.add('pk');
                let username = officer.username;
                const usernameTd = document.createElement('td');
                if(username == null) {
                    username = 'null';
                    usernameTd.classList.add('null');
                }
                usernameTd.innerText = username;
                const online = officer.online;
                const onlineTd = document.createElement('td');
                onlineTd.innerText = online;
                onlineTd.classList.add(online ? 'true' : 'false');
                const statusTd = document.createElement('td');
                statusTd.innerText = officer.status;
                setColor(statusTd, officer.statusColor);
                const rankTd = document.createElement('td');
                rankTd.innerText = officer.rank;
                setColor(rankTd, officer.rankColor);
                const callsignTd = document.createElement('td');
                const callsign = officer.callsign;
                callsignTd.innerText = callsign != null ? callsign : 'null';
                callsignTd.classList.add(callsign != null ? 'callsign' : 'null');
                const callsignReserved = officer.callsignReserved;
                const callsignReservedTd = document.createElement('td');
                callsignReservedTd.innerText = callsignReserved;
                callsignReservedTd.classList.add(callsignReserved ? 'true' : 'false');
                tr.appendChild(uuidTd);
                tr.appendChild(usernameTd);
                tr.appendChild(onlineTd);
                tr.appendChild(statusTd);
                tr.appendChild(rankTd);
                tr.appendChild(callsignTd);
                tr.appendChild(callsignReservedTd);
                officers.appendChild(tr);
            }
            handleHeader(order);
            handleFooter(page, res.pages);
        },
        statusCode: statusCodeActions
    });
}