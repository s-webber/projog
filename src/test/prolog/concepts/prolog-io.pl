% Write Prolog syntax to a file.
%?- open('io_test.tmp', write, Z), put_char(b), set_output(Z), write(a(1,2,3,[a,b])), put_char('.'), close(Z)
%OUTPUT b
% Z=io_test.tmp_output_handle

% Read the contents of the newly written file.

%?- open('io_test.tmp', read, Z), set_input(Z), read(Y), close(Z)
% Y=a(1, 2, 3, [a,b])
% Z=io_test.tmp_input_handle

% "Consult" the facts defined in the newly written file.

%TRUE consult('io_test.tmp')

% Perform a query which uses the facts consulted from the newly written file.

%?- a(1, X, 3, [a,b])
% X=2

% Confirm streams and reset them.

%?- current_input(X)
% X=io_test.tmp_input_handle
%TRUE set_input('user_input')
%TRUE current_input('user_input')

% Note: "seeing" is a synonym for "current_input".
%TRUE seeing('user_input')

%TRUE set_output('user_output')

% Example of an error when the file to be read does not actually exist.

%?- open('directory_that_doesnt_exist/some_file.xyz','read',Z)
%ERROR Unable to open input for: directory_that_doesnt_exist/some_file.xyz

% "see/1" is a convenient way, with a single statement, to both open an input stream and set it as the current input stream. 
%TRUE see('io_test.tmp')

%?- get_char(X)
% X=a

%?- current_input(X)
% X=io_test.tmp_input_handle

% "seen" is a convenient way, with a single statement, to both close the current input stream and set user_input as the current input stream.
%TRUE seen

%?- current_input(X)
% X=user_input

% If the argument of "see/1" is a file handle, rather than a filename, then the current input stream is set to the stream represented by the handle.
%?- open('io_test.tmp', read, W), see(W), current_input(X), get_char(Y), seen, current_input(Z)
% W=io_test.tmp_input_handle
% X=io_test.tmp_input_handle
% Y=a
% Z=user_input

% "tell/1" is a convenient way, with a single statement, to both open an output stream and set it as the current output stream. 
% "told" is a convenient way, with a single statement, to both close the current output stream and set user_output as the current output stream.
%?- tell('io_test.tmp'), put_char(x), told, see('io_test.tmp'), get_char(Y), seen
% Y=x
