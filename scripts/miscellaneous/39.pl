% test consult adds ".pl" to resource if no file extension provided

%QUERY open('build/39test.pl', write, Z), set_output(Z), write('?- asserta(test).'), close(Z)
%ANSWER Z=build/39test.pl_output_handle

%FALSE test

%TRUE consult('build/39test')

%TRUE test