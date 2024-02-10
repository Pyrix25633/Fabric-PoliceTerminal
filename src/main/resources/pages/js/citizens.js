import { initTable, setHandler } from "./init-table.js";
import { loadSettings, cachedLogin, statusCodeActions } from "./load-settings.js";

const citizens = initTable(null, ['UUID', 'Username', 'Online']);

let settings;

loadSettings((sett) => {
    settings = sett;
}, true);

setHandler((page, handleFooter) => {
    $.ajax({
        url: '/api/citizen/list',
        method: 'POST',
        data: JSON.stringify({
            uuid: cachedLogin.uuid,
            token: cachedLogin.token,
            page: page
        }),
        contentType: 'application/json',
        success: (res) => {
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