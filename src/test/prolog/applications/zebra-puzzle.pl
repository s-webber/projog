solve(People,Doors,Pets,Cigarettes,Drink) :-
  People = [english-English,spainiard-Spainiard,ukrainian-Ukrainian,norwegian-Norwegian,japanese-Japanese],
  init(People),
  Doors = [red-Red,green-Green,ivory-Ivory,yellow-Yellow,blue-Blue],
  init(Doors),
  Pets = [dog-Dog,snails-Snails,fox-Fox,horse-Horse,zebra-_],
  init(Pets),
  Cigarettes = [oldGold-OldGold,kools-Kools,chesterfields-Chesterfields,luckyStrike-LuckyStrike,parliaments-Parliaments],
  init(Cigarettes),
  Drink = [coffee-Coffee,tea-Tea,milk-Milk,juice-Juice,water-_],
  init(Drink),
  % The Englishman lives in the red house.
  English #= Red,
  % The Spaniard owns the dog.
  Spainiard #= Dog,
  % Coffee is drunk in the green house.
  Coffee #= Green,
  % The Ukrainian drinks tea.
  Ukrainian #= Tea,
  % The green house is immediately to the right of the ivory house.
  Green #= Ivory+1,
  % The Old Gold smoker owns snails.
  OldGold #= Snails,
  % Kools are smoked in the yellow house.
  Kools #= Yellow,
  % Milk is drunk in the middle house.
  Milk #= 3,
  % The Norwegian lives in the first house.
  Norwegian #= 1,
  % The man who smokes Chesterfields lives in the house next to the man with the fox.
  abs(Chesterfields - Fox) #= 1,
  % Kools are smoked in the house next to the house where the horse is kept.
  abs(Kools - Horse) #= 1,
  % The Lucky Strike smoker drinks orange juice.
  LuckyStrike #= Juice,
  % The Japanese smokes Parliaments.
  Japanese #= Parliaments,
  % The Norwegian lives next to the blue house.
  abs(Norwegian - Blue) #= 1,
  flatten([People,Doors,Pets,Cigarettes,Drink],ObjectPairs),
  pairs_values(ObjectPairs,Variables),
  label(Variables).

init(ObjectPairs) :-
  pairs_values(ObjectPairs,Variables),
  Variables ins 1..5,
  all_different(Variables).

%?- solve(People,Doors,Pets,Cigarettes,Drink)
% Cigarettes = [oldGold - 3,kools - 1,chesterfields - 2,luckyStrike - 4,parliaments - 5]
% Doors = [red - 3,green - 5,ivory - 4,yellow - 1,blue - 2]
% Drink = [coffee - 5,tea - 2,milk - 3,juice - 4,water - 1]
% People = [english - 3,spainiard - 4,ukrainian - 2,norwegian - 1,japanese - 5]
% Pets = [dog - 4,snails - 3,fox - 1,horse - 2,zebra - 5]
%NO
