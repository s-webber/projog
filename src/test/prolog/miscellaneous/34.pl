isEven(X) :- 0 is X mod 2.

%TRUE isEven(0)
%TRUE isEven(-2)
%TRUE isEven(2)
%TRUE isEven(876574)

%FAIL isEven(1)
%FAIL isEven(-1)
%FAIL isEven(3)
%FAIL isEven(876575)
