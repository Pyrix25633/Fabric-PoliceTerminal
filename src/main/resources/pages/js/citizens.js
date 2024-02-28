import { initTable, setHandler } from "./init-table.js";
import { loadSettings, statusCodeActions } from "./load-settings.js";

const citizens = initTable(null, [
    {text: 'UUID', order: 'uuid', search: 'uuid'},
    {text: 'Username', order: 'username', search: 'username'},
    {text: 'Online', order: 'online', search: 'online'}
]);

let settings;

loadSettings((sett) => {
    settings = sett;
}, true);

setHandler((page, order, handleHeader, handleFooter) => {
    $.ajax({
        url: '/api/citizens',
        method: 'GET',
        data: {
            page: page,
            order: order
        },
        success: (res) => {
            handleHeader(order);
            handleFooter(page, res.pages);
            citizens.innerHTML = '';
            for(const citizen of res.citizens) {
                const tr = document.createElement('tr');
                const uuidTd = document.createElement('td');
                uuidTd.innerText = citizen.uuid;
                uuidTd.classList.add('pk');
                let username = citizen.username;
                const usernameTd = document.createElement('td');
                if(username == null) {
                    username = 'null';
                    usernameTd.classList.add('null');
                }
                usernameTd.innerText = username;
                const online = citizen.online;
                const onlineTd = document.createElement('td');
                onlineTd.innerText = online;
                onlineTd.classList.add(online ? 'true' : 'false');
                tr.appendChild(uuidTd);
                tr.appendChild(usernameTd);
                tr.appendChild(onlineTd);
                citizens.appendChild(tr);
            }
        },
        statusCode: statusCodeActions
    });
});