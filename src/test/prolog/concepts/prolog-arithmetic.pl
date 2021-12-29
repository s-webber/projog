%?- X is 45345
% X=45345

%?- X is -876878
% X=-876878

%?- X is 2+1
% X=3

%?- X is 12-5
% X=7

%?- X is 6*7
% X=42

%?- X is 6/2
% X=3

%?- X is 6//2
% X=3

%?- X is 7/2
% X=3.5

%?- X is 7//2
% X=3

%?- X is 7 * 4 // 2
% X=14

%?- X is 5 mod 3
% X=2

%?- X is 2147483647 + 1
% X=2147483648

%?- X is -2147483648 - 1
% X=-2147483649

% The maximum integer value that can be represented is 9223372036854775807

%?- X is 9223372036854775807
% X=9223372036854775807

%?- X is 9223372036854775806 + 1
% X=9223372036854775807

%?- X is 9223372036854775806 + 2
% X=-9223372036854775808

% The minimum integer value that can be represented is -9223372036854775808

%?- X is -9223372036854775808
% X=-9223372036854775808

%?- X is -9223372036854775807 - 1
% X=-9223372036854775808

%?- X is -9223372036854775807 - 2
% X=9223372036854775807

% Prolog evaluates numerical expressions using the BODMAS rule to determine the order in which operations are performed.

%?- X is 1+2*3
% X=7

%?- X is 2*3+1
% X=7

%?- X is (1+2)*3
% X=9

%?- X is 2*(3+1)
% X=8

%?- X is -(4+6)
% X=-10

% Variables must be instantiated to numerical terms before they can be used in calculations.

%?- X is 1 + Y
%ERROR Cannot get Numeric for term: Y of type: VARIABLE

%?- Y = 4, X is 1 + Y
% Y=4
% X=5

%?- Y = 6-2, X is 1 + Y
% Y=6 - 2
% X=5

%?- Y = 2*4, X is -Y
% Y=2 * 4
% X=-8

% Examples of using decimal point numbers in calculations.

%?- X is 1.3 + 1.2
% X=2.5

%?- X is 1.3 + 1
% X=2.3

%?- X is 1 + 1.2
% X=2.2

%?- X is 3.5 - 1.25
% X=2.25

%?- X is 3.5 - 1
% X=2.5

%?- X is 3 - 1.25
% X=1.75

%?- X is -9.6
% X=-9.6

%?- Y = 9.6, X is -Y
% Y=9.6
% X=-9.6

%?- X is 6.3 * 2.75
% X=17.325

%?- X is 6.3 * 2
% X=12.6

%?- X is 6 * 2.75
% X=16.5

%?- X is 8.5 / 2.5
% X=3.4

%?- X is 8.5 / 2
% X=4.25

%?- X is 8 / 2.5
% X=3.2

%?- X is 5 mod 2
% X=1

%?- X is 5 mod -2
% X=-1

%?- X is -5 mod -2
% X=-1

%?- X is -5 mod 2
% X=1

%?- X is 5 rem 2
% X=1

%?- X is 5 rem -2
% X=1

%?- X is -5 rem -2
% X=-1

%?- X is -5 rem 2
% X=-1

%?- X is 2 mod -5
% X=-3

%?- X is 2 rem -5
% X=2


