import { boardHandler } from './board.js'
import { game } from './game.js'
import { getBgColor } from './color.js'

const board = document.getElementById('board')
const scores = document.getElementById('scores')
const cells = [] 
let enabled = true

initBoard()

document.addEventListener('keydown', (event) => {
    if (enabled) {
        const hasChanged = boardHandler.moveAll(event.key)
        if (hasChanged) {
            boardHandler.spawnNewTileRandomly()
            render()
        }
    }
})

/* Functions */
function initBoard(){
    let rows = 4
    let cols = 4
    const elements = rows * cols;
    for (let i = 0; i < elements; i++) {
        let cell = document.createElement('div')
        cell.className = 'cell'
        board.appendChild(cell)
        cells.push(cell)
    }
    render()
}

function render() {
    const data = boardHandler.getBoardCopy()
    const rows = data.length
    const cols = data[0].length
    for (let row = 0; row < rows; row++) {
        for (let col = 0; col < cols; col++) {
            const tile = cells[row*rows + col]
            const value = data[row][col]
            tile.textContent = value === 0 ? '' : value
           tile.style.backgroundColor = getBgColor(value)
        }
    }
    debugger
    scores.textContent = game.getOverall().scores
    if(game.isWon()) {
        enabled = false
        createWinScreen()
    }
    if(game.isLost()) {
        enabled = false
        createLostScreen()
    }
}

function createLostScreen() {
    const lostMenu = document.createElement('div');
    lostMenu.className = 'after-game-menu'
    const {
        scores, moves
    } = game.getOverall()
    lostMenu.innerHTML = 
    `<div id="after-game-message">
        You have lost with ${scores} points and ${moves} moves
    </div>
    <div id="after-game-button-section">
        <button id="new-game-button">New game</button>
        <button id="menu-button">Menu</button>
    </div>
    `
    board.after(lostMenu)
}

function createWinScreen() {
    const winMenu = document.createElement('div');
    winMenu.className = 'after-game-menu'
    const {
        scores, moves
    } = game.getOverall()
    winMenu.innerHTML = 
    `<div id="after-game-message">
        You have won with ${scores} points and ${moves} moves
    </div>
    <div id="after-game-button-section">
        <button id="new-game-button">New game</button>
        <button id="menu-button">Menu</button>
    </div>
    `
    board.after(winMenu)
}