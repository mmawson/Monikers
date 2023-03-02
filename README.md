# Monikers
It’s a game where each person playing puts some names on slips of paper and there are two teams that take it in turns trying to guess. One person will pick up slips of paper one at a time and give their team clues, and their team will try to guess. If they get the name correct, you move on to the next one, and the next one, until the time for your turn is out.
So basically, what our app would need to do is:
Before the game starts, allow the user to enter words that will be added to the word bank which will then be hidden
Once the game starts, randomly show the user one of the remaining words from the word bank and start a timer. They have the option of hitting a ‘Skip’ button or a ‘Correct’ button, based on if their teammates have guessed the word or if they want to skip the word and try to get the next one. Once the timer runs out, their turn is over, and play moves to the next team. In the simplest implementation, they would just pass the phone to the next person so they can take the next turn
Once all the words have been used up 3 times, add up all the points for each team and see who wins
Have a settings page that allows the user to set settings like number of skips you can use, amount of time per turn, etc.
Have a “How to Play” page that describes how to play
That’s the simplest implementation, where the whole game happens on one person’s phone, but if we want to go further, or if the professor thinks it needs to be more complex, we can add:
6. Allow one user to host a private game that other users can join
7. Once they’ve joined, anyone in the game can add words to the word bank simultaneously, before the game has started
8. Once the game starts, only one phone is the ‘Active’ phone, which is the phone that shows the words on it. The other phones just show the timer counting down. At any point between turns, one of the players can make their phone the ‘Active’ phone, so they don’t need to pass one phone around
