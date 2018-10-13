word2chars([],[]).
word2chars([Word|RestWords],[Chars|RestChars]) :-
  atom_chars(Word,Chars),
  word2chars(RestWords,RestChars).

test(X, Y) :- word2chars(X, Y).

%QUERY word2chars(X, [[d,o],[o,r,e],[m,a],[l,i,s],[u,r],[a,s],[p,o],[s,o],[p,i,r,u,s],[o,k,e,r],[a,l],[a,d,a,m],[i,k]])
%ANSWER X = [do,ore,ma,lis,ur,as,po,so,pirus,oker,al,adam,ik]

% TODO Remove ! from query once compiled mode is smart eniugh to know that word2chars is not retryable.
% Currently interpreted mode does detect this but compiled mode doesn't - which is why without the ! there is a difference
% in behaviour when this test is run in the two different modes.
%QUERY test(X, [[d,o],[o,r,e],[m,a],[l,i,s],[u,r],[a,s],[p,o],[s,o],[p,i,r,u,s],[o,k,e,r],[a,l],[a,d,a,m],[i,k]]), !
%ANSWER X = [do,ore,ma,lis,ur,as,po,so,pirus,oker,al,adam,ik]
%NO
