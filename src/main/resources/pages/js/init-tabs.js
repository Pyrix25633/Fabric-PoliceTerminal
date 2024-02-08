const tabs = ['index', 'citizens', 'officers', 'callouts', 'manual'];

const current = /^.*\/(.*)$/.exec(location.href)[1];

document.getElementById('tabs').innerHTML = `
    <div class="tab container" id="tab-index">
        <span class="title police-color">POLICE</span>
        <span class="title foreground-color">TERMINAL</span>
        <img id="icon" src="./img/icon.svg" alt="Police Terminal Icon">
    </div>
    <div class="tab container" id="tab-citizens">
        <span class="title">Citizens</span>
        <img class="icon" src="./img/citizens.svg" alt="Citizens Icon">
    </div>
    <div class="tab container" id="tab-officers">
        <span class="title">Officers</span>
        <img class="icon" src="./img/officers.svg" alt="Officers Icon">
    </div>
    <div class="tab container" id="tab-callouts">
        <span class="title">Callouts</span>
        <img class="icon" src="./img/callouts.svg" alt="Callouts Icon">
    </div>
    <div class="tab container" id="tab-manual">
        <span class="title">Manual</span>
        <img class="icon" src="./img/manual.svg" alt="Manual Icon">
    </div>
`;

for(const tab of tabs) {
    const div = document.getElementById('tab-' + tab);
    if(tab == current || (current == '' && tab == 'index'))
        div.classList.add('active');
    else
        div.addEventListener('click', () => {location.href = '/' + (tab == 'index' ? '' : tab)});
}