% %QUERY% X is 45345
% %ANSWER% X=45345

% %QUERY% X is -876878
% %ANSWER% X=-876878

% %QUERY% X is 2+1
% %ANSWER% X=3

% %QUERY% X is 12-5
% %ANSWER% X=7

% %QUERY% X is 6*7
% %ANSWER% X=42

% %QUERY% X is 5/3
% %ANSWER% X=1

% %QUERY% X is 5 mod 3
% %ANSWER% X=2

% Prolog evaluates numerical expressions using the BODMAS rule to determine the order in which operations are performed.

% %QUERY% X is 1+2*3
% %ANSWER% X=7

% %QUERY% X is 2*3+1
% %ANSWER% X=7

% %QUERY% X is (1+2)*3
% %ANSWER% X=9

% %QUERY% X is 2*(3+1)
% %ANSWER% X=8

% %QUERY% X is -(4+6)
% %ANSWER% X=-10

% Variables must be instantiated to numerical terms before they can be used in calculations.

% %QUERY% X is 1 + Y
% %EXCEPTION% Can't get Numeric for term: Y of type: NAMED_VARIABLE

% %QUERY% Y = 4, X is 1 + Y
% %ANSWER%
% Y=4
% X=5
% %ANSWER%

% %QUERY% Y = 6-2, X is 1 + Y
% %ANSWER%
% Y=6 - 2
% X=5
% %ANSWER%

% %QUERY% Y = 2*4, X is -Y
% %ANSWER%
% Y=2 * 4
% X=-8
% %ANSWER%

% Examples of using decimal point numbers in calculations.

% %QUERY% X is 1.3 + 1.2
% %ANSWER% X=2.5

% %QUERY% X is 1.3 + 1
% %ANSWER% X=2.3

% %QUERY% X is 1 + 1.2
% %ANSWER% X=2.2

% %QUERY% X is 3.5 - 1.25
% %ANSWER% X=2.25

% %QUERY% X is 3.5 - 1
% %ANSWER% X=2.5

% %QUERY% X is 3 - 1.25
% %ANSWER% X=1.75

% %QUERY% X is -9.6
% %ANSWER% X=-9.6

% %QUERY% Y = 9.6, X is -Y
% %ANSWER%
% Y=9.6
% X=-9.6
% %ANSWER%

% %QUERY% X is 6.3 * 2.75
% %ANSWER% X=17.325

% %QUERY% X is 6.3 * 2
% %ANSWER% X=12.6

% %QUERY% X is 6 * 2.75
% %ANSWER% X=16.5

% %QUERY% X is 8.5 / 2.5
% %ANSWER% X=3.4

% %QUERY% X is 8.5 / 2
% %ANSWER% X=4.25

% %QUERY% X is 8 / 2.5
% %ANSWER% X=3.2

% %QUERY% X is 3.25 mod 2.5
% %ANSWER% X=0.75

% %QUERY% X is 3.25 mod 2
% %ANSWER% X=1.25

% %QUERY% X is 3 mod 2.5
% %ANSWER% X=0.5
