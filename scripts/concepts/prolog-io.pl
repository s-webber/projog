% Write Prolog syntax to a file.
%QUERY open('io_test.tmp', write, Z), put_char(b), set_output(Z), write(a(1,2,3,[a,b])), put_char('.'), close(Z)
%OUTPUT b
%ANSWER Z = io_test.tmp_output_handle

% Read the contents of the newly written file.

%QUERY open('io_test.tmp', read, Z), set_input(Z), read(Y), close(Z)
%ANSWER
% Y = a(1, 2, 3, [a,b])
% Z = io_test.tmp_input_handle
%ANSWER

% "Consult" the facts defined in the newly written file.

%TRUE consult('io_test.tmp')

% Perform a query which uses the facts consulted from the newly written file.

%QUERY a(1, X, 3, [a,b])
%ANSWER X = 2

% Confirm streams and reset them.

%QUERY current_input(X)
%ANSWER X = io_test.tmp_input_handle
%TRUE set_input('user_input')
%TRUE current_input('user_input')

% Note: "seeing" is a synonym for "current_input".
%TRUE seeing('user_input')

%TRUE set_output('user_output')

% Example of an error when the file to be read does not actually exist.

%QUERY open('directory_that_doesnt_exist/some_file.xyz','read',Z)
%ERROR Unable to open input for: directory_that_doesnt_exist/some_file.xyz