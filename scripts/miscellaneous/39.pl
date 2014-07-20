% test consult adds ".pl" to resource if no file extension provided

%QUERY open('build/39test.pl', write, Z), set_output(Z), write('?- asserta(test).'), close(Z)
%ANSWER Z=build/39test.pl_output_handle

%FALSE test

%TRUE consult('build/39test')

%TRUE test

% test ensure_loaded is a synonym for consult

%TRUE ensure_loaded('build/39test')

%QUERY test
%ANSWER/
%ANSWER/

%TRUE ensure_loaded('build/39test.pl')

%QUERY test
%ANSWER/
%ANSWER/
%ANSWER/

%QUERY ensure_loaded('build/doesntexist')
%ERROR Could not read prolog source from resource: build/doesntexist.pl

%QUERY ensure_loaded('build/39test.pro')
%ERROR Could not read prolog source from resource: build/39test.pro