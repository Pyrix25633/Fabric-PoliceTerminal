import { loadSettings, cachedLogin, statusCodeActions } from "./load-settings.js";

let settings;

loadSettings((sett) => {
    console.log("Loaded settings");
}, true);