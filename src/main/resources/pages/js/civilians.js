import { initTable } from "./init-table.js";
import { loadSettings, cachedLogin, statusCodeActions } from "./load-settings.js";

const civilians = initTable(['UUID', 'Username', 'Online']);

let settings;

loadSettings((sett) => {
    settings = sett;
}, true);