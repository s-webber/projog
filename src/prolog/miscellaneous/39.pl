%QUERY open('build/39test.pl', write, Z), set_output(Z), write('?- asserta(test).'), close(Z)
%ANSWER Z=build/39test.pl_output_handle

%FALSE test

%QUERY consult('build/39test')
%ERROR Could not read prolog source from resource: build/39test

%TRUE consult('build/39test.pl')

%TRUE test

% test ensure_loaded adds ".pl" to resource if no file extension provided

%TRUE ensure_loaded('build/39test')

%QUERY test
%ANSWER/
%ANSWER/

%TRUE ensure_loaded('build/39test.pl')

%QUERY test
%ANSWER/
%ANSWER/

%QUERY ensure_loaded('build/doesntexist')
%ERROR Could not read prolog source from resource: build/doesntexist.pl

%QUERY ensure_loaded('build/39test.pro')
%ERROR Could not read prolog source from resource: build/39test.pro