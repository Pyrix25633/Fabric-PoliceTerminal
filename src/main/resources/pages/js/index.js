import { loadSettings, cachedLogin, statusCodeActions } from "./load-settings.js";

let settings;

loadSettings((sett) => {
    settings = sett;
}, true);