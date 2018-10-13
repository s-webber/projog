word2chars([],[]).
word2chars([Word|RestWords],[Chars|RestChars]) :-
  atom_chars(Word,Chars),
  word2chars(RestWords,RestChars).

test(X, Y) :- word2chars(X, Y).

%QUERY word2chars(X, [[d,o],[o,r,e],[m,a],[l,i,s],[u,r],[a,s],[p,o],[s,o],[p,i,r,u,s],[o,k,e,r],[a,l],[a,d,a,m],[i,k]])
%ANSWER X = [do,ore,ma,lis,ur,as,po,so,pirus,oker,al,adam,ik]

%QUERY test(X, [[d,o],[o,r,e],[m,a],[l,i,s],[u,r],[a,s],[p,o],[s,o],[p,i,r,u,s],[o,k,e,r],[a,l],[a,d,a,m],[i,k]])
%ANSWER X = [do,ore,ma,lis,ur,as,po,so,pirus,oker,al,adam,ik]
%NO
