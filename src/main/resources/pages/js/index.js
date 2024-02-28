import { loadSettings } from "./load-settings.js";

let settings;

loadSettings((sett) => {
    settings = sett;
}, true);