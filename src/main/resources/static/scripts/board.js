import { deepCopy2D, deepEqual2D } from './arrayUtils.js'
import { getRandomTileValue } from './random.js'
import { game } from './game.js'

class Board {

    #board
    #rows
    #cols
    #moves
    #game

    constructor(game) {
        this.#game = game
        
        this.#board = [
            [2, 0, 0, 0],
            [0, 2, 0, 0],
            [0, 4, 0, 0],
            [0, 0, 0, 0]
        ]

        this.#rows = this.#board?.length
        this.#cols = this.#board[0]?.length
        this.#moves = {
            'ArrowRight' : () => this.#moveAllRight(),
            'ArrowLeft' : () => this.#moveAllLeft(),
            'ArrowUp' : () => this.#moveAllUp(),
            'ArrowDown' : () => this.#moveAllDown()
        }
    }

    moveAll(key) {
        const copy = deepCopy2D(this.#board)
        this.#moves[key]?.()
        return !deepEqual2D(this.#board, copy)
    }

    #moveAllRight() {
        for (let row = 0; row < this.#rows; row++) {
            for (let col = this.#cols - 2; col >= 0; col--) {
                if (this.#board[row][col] != 0) {
                    let wasChanged = false
                    for (let k = col + 1; k <= this.#cols - 1; k++) {
                        let value = this.#board[row][k]
                        if (value != 0) {
                            if (value === this.#board[row][col]) {
                                this.#mergeCells(row, col, row, k)
                            } else {
                                this.#move(row, col, row, k - 1)
                            }
                            wasChanged = true
                            break;
                        }
                    }
                    if (!wasChanged) {
                        this.#move(row, col, row, this.#cols - 1)
                    }
                }
            }
        }
    }

    #moveAllLeft() {
        for (let row = 0; row < this.#rows; row++) {
            for (let col = 0; col < this.#cols; col++) {
                if (this.#board[row][col] != 0) {
                    let wasChanged = false
                    for (let k = col - 1; k >= 0; k--) {
                        let value = this.#board[row][k]
                        if (value != 0) {
                            if (value === this.#board[row][col]) {
                                this.#mergeCells(row, col, row, k)
                            } else {
                                this.#move(row, col, row, k + 1)
                            }
                            wasChanged = true
                            break;
                        }
                    }
                    if (!wasChanged) {
                        this.#move(row, col, row, 0)
                    }
                }
            }
        }
    }

    #moveAllUp() {
        for (let col = 0; col < this.#cols; col++) {
            for (let row = 0; row < this.#rows; row++) {
                if (this.#board[row][col] != 0) {
                    let wasChanged = false
                    for (let k = row - 1; k >= 0; k--) {
                        let value = this.#board[k][col]
                        if (value != 0) {
                            if (value === this.#board[row][col]) {
                                this.#mergeCells(row, col, k, col)
                            } else {
                                this.#move(row, col, k + 1, col)
                            }
                            wasChanged = true
                            break;
                        }
                    }
                    if (!wasChanged) {
                        this.#move(row, col, 0, col)
                    }
                }
            }
        }
    }

    #moveAllDown() {
        let hasChangedGlobally = false
        for (let col = 0; col < this.#cols; col++) {
            for (let row = this.#rows - 1; row >= 0; row--) {
                if (this.#board[row][col] != 0) {
                    let wasChanged = false
                    for (let k = row + 1; k < this.#rows; k++) {
                        let value = this.#board[k][col]
                        if (value != 0) {
                            if (value === this.#board[row][col]) {
                                this.#mergeCells(row, col, k, col)
                            } else {
                                this.#move(row, col, k - 1, col)
                            }
                            wasChanged = true
                            break;
                        }
                    }
                    if (!wasChanged) {
                        this.#move(row, col, this.#rows - 1, col)
                    }
                }
            }
        }
    }

    setBoard(nextBoard) {
        if (!Array.isArray(nextBoard) || nextBoard.length !== this.#rows) return
        const normalized = nextBoard.map((row) =>
            Array.isArray(row)
                ? row.slice(0, this.#cols).map((value) => Number(value) || 0)
                : Array(this.#cols).fill(0)
        )
        while (normalized.length < this.#rows) {
            normalized.push(Array(this.#cols).fill(0))
        }
        this.#board = normalized
    }

    getBoardCopy() {
        return deepCopy2D(this.#board)
    }

    loadBoard(board) {
        this.setBoard(board)
    }

    spawnNewTileRandomly() {
        const freeCells = this.#findFreeCells()
        if (freeCells?.length > 0) {
            let index = Math.floor(Math.random() * freeCells.length)
            let [row, col] = freeCells[index]
            let value = getRandomTileValue()
            this.#board[row][col] = value
        }
        if (!this.#hasAvailableMoves()) {
            this.#game.setGameLost()
        }
    }

    getGameSnapshot() {
        return {
            board: this.getBoardCopy(),
            game: this.#game.getOverall()
        }
    }

    #findFreeCells() {
        let freeCells = []
        for(let row = 0; row < this.#rows; row++) {
            for(let col = 0; col < this.#cols; col++){
                if(this.#board[row][col] === 0) {
                    freeCells.push([row, col])
                }
            }
        }
        return freeCells
    }

    #hasAvailableMoves() {
        for (let row = 0; row < this.#rows; row++) {
            for (let col = 0; col < this.#cols; col++) {
                if (this.#board[row][col] === 0) return true
                if (col + 1 < this.#cols && this.#board[row][col] === this.#board[row][col + 1]) return true
                if (row + 1 < this.#rows && this.#board[row][col] === this.#board[row + 1][col]) return true
            }
        }
        return false
    }

    #mergeCells(fromRow, fromCol, toRow, toCol){
        this.#board[toRow][toCol] *= 2
        this.#board[fromRow][fromCol] = 0
        this.#game.addScores(this.#board[toRow][toCol])
        if(this.#board[toRow][toCol] === 2048) {
            this.#game.setGameWon()
        }
    }

    #move(fromRow, fromCol, toRow, toCol) {
        let tmp = this.#board[fromRow][fromCol]
        this.#board[fromRow][fromCol] = 0
        this.#board[toRow][toCol] = tmp
    }
}


export const boardHandler = new Board(game)
