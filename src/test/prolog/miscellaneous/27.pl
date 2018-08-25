test(Coins, ExistingTotal, Target) :-
	[Coin|_]=Coins,
	NewTotal is ExistingTotal + Coin,
	NewTotal =< Target.

%TRUE test([4,7,8], 12, 17)
%TRUE test([4,7,8], 13, 17)
%FALSE test([4,7,8], 14, 17)
