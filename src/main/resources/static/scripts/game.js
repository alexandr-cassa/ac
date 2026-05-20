
class Game {
    
    #scores
    #state
    #moves

    constructor() {
        this.#scores = 0
        this.#moves = 0
        this.#state = 'IN_PROGRESS'
    }

    addScores(value) {
        if (value > 0) {
            this.#scores += value
        }
    }

    incMoves() {
        this.#moves++
    }

    setGameWon() {
        this.#state = 'WON'
    }

    setGameLost() {
        this.#state = 'LOST'
    }

    isWon() {
        return this.#state === 'WON'
    }

    isLost() {
        return this.#state === 'LOST'
    }

    getOverall() {
        return {
            scores: this.#scores,
            state: this.#state,
            moves: this.#moves
        }
    }

    loadState(scores, moves, status) {
        this.#scores = scores || 0;
        this.#moves = moves || 0;
        this.#state = status || 'IN_PROGRESS';
    }
}

export const game = new Game()
