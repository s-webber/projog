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

% "see/1" is a convenient way, with a single statement, to both open an input stream and set it as the current input stream. 
%TRUE see('io_test.tmp')
 
%QUERY get_char(X)
%ANSWER X=a

%QUERY current_input(X)
%ANSWER X=io_test.tmp_input_handle

% "seen" is a convenient way, with a single statement, to both close the current input stream and set user_input as the current input stream.
%TRUE seen

%QUERY current_input(X)
%ANSWER X=user_input

% If the argument of "see/1" is a file handle, rather than a filename, then the current input stream is set to the stream represented by the handle.
%QUERY open('io_test.tmp', read, W), see(W), current_input(X), get_char(Y), seen, current_input(Z)
%ANSWER
% W=io_test.tmp_input_handle
% X=io_test.tmp_input_handle
% Y=a
% Z=user_input
%ANSWER

% "tell/1" is a convenient way, with a single statement, to both open an output stream and set it as the current output stream. 
% "told" is a convenient way, with a single statement, to both close the current output stream and set user_output as the current output stream.
%QUERY tell('io_test.tmp'), put_char(x), told, see('io_test.tmp'), get_char(Y), seen
%ANSWER Y=x
