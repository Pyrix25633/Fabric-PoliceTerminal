import { loadSettings, cachedLogin, statusCodeActions } from "./load-settings.js";

let settings;

alert("Test");

loadSettings((sett) => {
    settings = sett;
    $.ajax({
        url: '/api/chat/list',
        method: 'POST',
        data: JSON.stringify(cachedLogin),
        contentType: 'application/json',
        success: (res) => {
            const chats = Object.keys(res.chats);
            if(chats.length > 0)
                loadChatsInfo(chats, res.chats);
            else
                disableLoadingDiv();
        },
        statusCode: statusCodeActions
    });
}, true);