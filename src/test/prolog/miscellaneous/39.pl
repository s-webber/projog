%QUERY open('target/39test.pl', write, Z), set_output(Z), write('?- asserta(test).'), close(Z)
%ANSWER Z=target/39test.pl_output_handle

%FALSE test

%QUERY consult('target/39test')
%ERROR Could not read prolog source from resource: target/39test

%TRUE consult('target/39test.pl')

%TRUE test

% test ensure_loaded adds ".pl" to resource if no file extension provided

%TRUE ensure_loaded('target/39test')

%QUERY test
%ANSWER/
%ANSWER/

%TRUE ensure_loaded('target/39test.pl')

%QUERY test
%ANSWER/
%ANSWER/

%QUERY ensure_loaded('target/doesntexist')
%ERROR Could not read prolog source from resource: target/doesntexist.pl

%QUERY ensure_loaded('target/39test.pro')
%ERROR Could not read prolog source from resource: target/39test.pro