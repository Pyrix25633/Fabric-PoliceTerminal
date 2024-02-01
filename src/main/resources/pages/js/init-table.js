const table = document.getElementById('table');

let pageInput;
let pageMaxSpan;

export function initTable(headers) {
    const thead = document.createElement('thead');
    const theadRow = document.createElement('tr');
    for(const header of headers) {
        const th = document.createElement('th');
        th.innerText = header;
        theadRow.appendChild(th);
    }
    thead.appendChild(theadRow);
    table.appendChild(thead);
    const tbody = document.createElement('tbody');
    table.appendChild(tbody);
    const tfoot = document.createElement('tfoot');
    tfoot.innerHTML = `
        <tr>
            <td colspan="3">
                <div class="container">
                    <img src="./img/page-first.svg" alt="Page First Icon" class="button">
                    <img src="./img/page-previous.svg" alt="Page Previous Icon" class="button">
                    <label for="page" id="page-label">Page</label>
                    <input type="text" id="page"> / <span id="page-max">8</span>
                    <img src="./img/page-next.svg" alt="Page Next Icon" class="button">
                    <img src="./img/page-last.svg" alt="Page Last Icon" class="button">
                </div>
            </td>
        </tr>
    `;
    pageInput = tfoot.getElementById('page');
    pageMaxSpan = tfoot.getElementById('page-max');
    table.appendChild(tfoot);
    return tbody;
}