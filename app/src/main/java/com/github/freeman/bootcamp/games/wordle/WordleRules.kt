package com.github.freeman.bootcamp.games.wordle

class WordleRules {
    private val rulesText = "The rules of Wordle are elegantly simple.\n" +
            "\n" +
            "Your objective is to guess a secret five-letter word in as few guesses as possible.\n" +
            "\n" +
            "To submit a guess, type any five-letter word and press enter.\n" +
            "\n" +
            "All of your guesses must be real words, according to a dictionary of five-letter words that Wordle allows as guesses. You can’t make up a non-existent word, like AEIOU, just to guess those letters.\n" +
            "\n" +
            "As soon as you’ve submitted your guess, the game will color-code each letter in your guess to tell you how close it was to the letters in the hidden word.\n" +
            "\n" +
            "A Red square means that this letter does not appear in the secret word at all\n" +
            "A yellow square means that this letter appears in the secret word, but it’s in the wrong spot within the word\n" +
            "A green square means that this letter appears in the secret word, and it’s in exactly the right place\n" +
            "Getting a green square or yellow square will get you closer to guessing the real secret word, since it means you’ve guessed a correct letter.\n" +
            "\n" +
            "For example, let’s say you guess “WRITE” and get two green squares on the W and the R, and gray squares for the I, T, and E. Your next guess might be WRONG, WRACK, or WRUNG, since these words start with WR and don’t contain the letters I, T, or E.\n" +
            "\n" +
            "Alternatively, let’s say you guess “WRITE” and get two green squares on the T and the E, and gray squares for the W, R, and I. In that case, your next guess might be BASTE, ELATE, or LATTE, since these words end with TE and don’t contain the letters W, R, or I.\n" +
            "\n" +
            "Remember that the same letter can appear multiple times in the secret word, and there’s no special color coding for letters that appear repeatedly. For example, if the secret word is BELLE and you guess a word with one L and one E, Wordle won’t tell you that both those letters actually appear twice.\n" +
            "\n" +
            "You get a maximum of six tries to guess the secret word. The game will give you a different winning statement depending on how many guesses it took:\n" +
            "\n" +
            "1 attempt: “Genius”\n" +
            "2 attempts: “Magnificent”\n" +
            "3 attempts: “Impressive”\n" +
            "4 attempts: “Splendid”\n" +
            "5 attempts: “Great”\n" +
            "6 attempts: “Phew”\n" +
            "Once you’ve found the secret word, Wordle has a “Share” option that lets you copy your results in the form of colored emoji cubes, so you can share them with anyone you want. Many players send their results to their Wordle-loving friends or post them on social media.\n" +
            "\n" +
            "What’s the Best Wordle Strategy?\n" +
            "If you want to try to win Wordle in as few attempts as possible, it’s a good idea to come up with a strategy that lets you test commonly used English letters in their most common positions within real five-letter words.\n" +
            "\n" +
            "For example, it’s smart to start with a word that includes the most commonly used letters in five-letter English words, such as A, E, S, T, R, and N. Words like SLATE and CRANE can be great starting options.\n" +
            "\n" +
            "Other people prefer to start with words that include as many vowels as possible, such as ADIEU or AUDIO. These words increase your chances of getting a green or yellow square on the first try, so you know right away which vowels will appear in the secret word.\n" +
            "\n" +
            "There are even AI algorithms that can analyze your Wordle strategy and recommend better words to try.\n" +
            "\n" +
            "Of course, you can always just guess whatever five-letter words you want. At the end of the day, Wordle is just a game, and you should play it in whichever way feels most fun."
}