% projog-bootstrap.pl
% This file contains Prolog syntax that is interpreted when a projog console is started.
% This file contains code that configures the projog environment with
% "core" built-in predicates (e.g. "true", "consult", etc.) and numerical operations (e.g. "+", "-", etc.).
% It also defines operators in order to provide a more convenient syntax for writing terms.
% This file is included in the projog-core.jar that contains the projog class files.
% This file can be overridden by providing another file named "projog-bootstrap.pl" 
% in the root directory where the console is launched, or in the classpath before the projog-core.jar.
% See http://projog.org/javadoc/org/projog/core/KnowledgeBaseUtils.html#bootstrap(org.projog.core.KnowledgeBase)

'?-'( pj_add_predicate('/'(op, 3), 'org.projog.core.predicate.builtin.io.Op') ).
'?-'( op(1200, fx, '?-') ).
?- op(400, yfx, '/').

% boolean
?- pj_add_predicate(true/0, 'org.projog.core.predicate.builtin.bool.True').
?- pj_add_predicate(fail/0, 'org.projog.core.predicate.builtin.bool.Fail').

% classify
?- pj_add_predicate(var/1, 'org.projog.core.predicate.builtin.classify.IsVar').
?- pj_add_predicate(nonvar/1, 'org.projog.core.predicate.builtin.classify.IsNonVar').
?- pj_add_predicate(atom/1, 'org.projog.core.predicate.builtin.classify.IsAtom').
?- pj_add_predicate(number/1, 'org.projog.core.predicate.builtin.classify.IsNumber').
?- pj_add_predicate(atomic/1, 'org.projog.core.predicate.builtin.classify.IsAtomic').
?- pj_add_predicate(integer/1, 'org.projog.core.predicate.builtin.classify.IsInteger').
?- pj_add_predicate(float/1, 'org.projog.core.predicate.builtin.classify.IsFloat').
?- pj_add_predicate(compound/1, 'org.projog.core.predicate.builtin.classify.IsCompound').
?- pj_add_predicate(is_list/1, 'org.projog.core.predicate.builtin.classify.IsList').
?- pj_add_predicate(char_type/2, 'org.projog.core.predicate.builtin.classify.CharType').

% compare
?- pj_add_predicate('='/2, 'org.projog.core.predicate.builtin.compare.Equal').
?- pj_add_predicate('=='/2, 'org.projog.core.predicate.builtin.compare.StrictEquality').
?- pj_add_predicate('\\=='/2, 'org.projog.core.predicate.builtin.compare.NotStrictEquality').
?- pj_add_predicate('=:='/2, 'org.projog.core.predicate.builtin.compare.NumericEquality').
?- pj_add_predicate('=\\='/2, 'org.projog.core.predicate.builtin.compare.NumericInequality').
?- pj_add_predicate('<'/2, 'org.projog.core.predicate.builtin.compare.NumericLessThan').
?- pj_add_predicate('=<'/2, 'org.projog.core.predicate.builtin.compare.NumericLessThanOrEqual').
?- pj_add_predicate('>'/2, 'org.projog.core.predicate.builtin.compare.NumericGreaterThan').
?- pj_add_predicate('>='/2, 'org.projog.core.predicate.builtin.compare.NumericGreaterThanOrEqual').
?- pj_add_predicate('@<'/2, 'org.projog.core.predicate.builtin.compare.TermLessThan').
?- pj_add_predicate('@>'/2, 'org.projog.core.predicate.builtin.compare.TermGreaterThan').
?- pj_add_predicate('@>='/2, 'org.projog.core.predicate.builtin.compare.TermGreaterThanOrEqual').
?- pj_add_predicate('@=<'/2, 'org.projog.core.predicate.builtin.compare.TermLessThanOrEqual').
?- pj_add_predicate('\\='/2, 'org.projog.core.predicate.builtin.compare.NotUnifiable').
?- pj_add_predicate(compare/3, 'org.projog.core.predicate.builtin.compare.Compare').
?- pj_add_predicate(predsort/3, 'org.projog.core.predicate.builtin.compare.PredSort').
?- pj_add_predicate(between/3, 'org.projog.core.predicate.builtin.compare.Between').
?- pj_add_predicate(unify_with_occurs_check/2, 'org.projog.core.predicate.builtin.compare.UnifyWithOccursCheck').
?- pj_add_predicate(is/2, 'org.projog.core.predicate.builtin.compare.Is').

% compound
?- pj_add_predicate(','/2, 'org.projog.core.predicate.builtin.compound.Conjunction').
?- pj_add_predicate(';'/2, 'org.projog.core.predicate.builtin.compound.Disjunction').
?- pj_add_predicate('/'('\\+', 1), 'org.projog.core.predicate.builtin.compound.Not').
?- pj_add_predicate(not/1, 'org.projog.core.predicate.builtin.compound.Not').
?- pj_add_predicate(call/1, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(call/2, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(call/3, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(call/4, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(call/5, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(call/6, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(call/7, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(call/8, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(call/9, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(call/10, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(time/1, 'org.projog.core.predicate.builtin.compound.Call').
?- pj_add_predicate(once/1, 'org.projog.core.predicate.builtin.compound.Once').
?- pj_add_predicate(bagof/3, 'org.projog.core.predicate.builtin.compound.BagOf').
?- pj_add_predicate(findall/3, 'org.projog.core.predicate.builtin.compound.FindAll').
?- pj_add_predicate(setof/3, 'org.projog.core.predicate.builtin.compound.SetOf').
?- pj_add_predicate('->'/2, 'org.projog.core.predicate.builtin.compound.IfThen').
?- pj_add_predicate(limit/2, 'org.projog.core.predicate.builtin.compound.Limit').

% construct
?- pj_add_predicate(functor/3, 'org.projog.core.predicate.builtin.construct.Functor').
?- pj_add_predicate(arg/3, 'org.projog.core.predicate.builtin.construct.Arg').
?- pj_add_predicate('=..'/2, 'org.projog.core.predicate.builtin.construct.Univ').
?- pj_add_predicate(atom_chars/2, 'org.projog.core.predicate.builtin.construct.TermSplit/atomChars').
?- pj_add_predicate(atom_codes/2, 'org.projog.core.predicate.builtin.construct.TermSplit/atomCodes').
?- pj_add_predicate(number_chars/2, 'org.projog.core.predicate.builtin.construct.TermSplit/numberChars').
?- pj_add_predicate(number_codes/2, 'org.projog.core.predicate.builtin.construct.TermSplit/numberCodes').
?- pj_add_predicate(atom_concat/3, 'org.projog.core.predicate.builtin.construct.AtomConcat').
?- pj_add_predicate(numbervars/1, 'org.projog.core.predicate.builtin.construct.NumberVars').
?- pj_add_predicate(numbervars/3, 'org.projog.core.predicate.builtin.construct.NumberVars').
?- pj_add_predicate(copy_term/2, 'org.projog.core.predicate.builtin.construct.CopyTerm').

% debug
?- pj_add_predicate(debugging/0, 'org.projog.core.predicate.builtin.debug.Debugging').
?- pj_add_predicate(nodebug/0, 'org.projog.core.predicate.builtin.debug.NoDebug').
?- pj_add_predicate(trace/0, 'org.projog.core.predicate.builtin.debug.Trace').
?- pj_add_predicate(notrace/0, 'org.projog.core.predicate.builtin.debug.NoTrace').
?- pj_add_predicate(spy/1, 'org.projog.core.predicate.builtin.debug.AlterSpyPoint/spy').
?- pj_add_predicate(nospy/1, 'org.projog.core.predicate.builtin.debug.AlterSpyPoint/noSpy').

% io
?- pj_add_predicate(close/1, 'org.projog.core.predicate.builtin.io.Close').
?- pj_add_predicate(current_input/1, 'org.projog.core.predicate.builtin.io.CurrentInput').
?- pj_add_predicate(seeing/1, 'org.projog.core.predicate.builtin.io.CurrentInput').
?- pj_add_predicate(see/1, 'org.projog.core.predicate.builtin.io.See').
?- pj_add_predicate(seen/0, 'org.projog.core.predicate.builtin.io.Seen').
?- pj_add_predicate(tab/1, 'org.projog.core.predicate.builtin.io.Tab').
?- pj_add_predicate(tell/1, 'org.projog.core.predicate.builtin.io.Tell').
?- pj_add_predicate(told/0, 'org.projog.core.predicate.builtin.io.Told').
?- pj_add_predicate(current_output/1, 'org.projog.core.predicate.builtin.io.CurrentOutput').
?- pj_add_predicate(get_char/1, 'org.projog.core.predicate.builtin.io.GetChar').
?- pj_add_predicate(get_code/1, 'org.projog.core.predicate.builtin.io.GetCode').
?- pj_add_predicate(get0/1, 'org.projog.core.predicate.builtin.io.GetCode').
?- pj_add_predicate(nl/0, 'org.projog.core.predicate.builtin.io.NewLine').
?- pj_add_predicate(open/3, 'org.projog.core.predicate.builtin.io.Open').
?- pj_add_predicate(put_char/1, 'org.projog.core.predicate.builtin.io.PutChar').
?- pj_add_predicate(read/1, 'org.projog.core.predicate.builtin.io.Read').
?- pj_add_predicate(set_input/1, 'org.projog.core.predicate.builtin.io.SetInput').
?- pj_add_predicate(set_output/1, 'org.projog.core.predicate.builtin.io.SetOutput').
?- pj_add_predicate(write/1, 'org.projog.core.predicate.builtin.io.Write/write').
?- pj_add_predicate(writeln/1, 'org.projog.core.predicate.builtin.io.Write/writeln').
?- pj_add_predicate(write_canonical/1, 'org.projog.core.predicate.builtin.io.WriteCanonical').
?- pj_add_predicate(writef/1, 'org.projog.core.predicate.builtin.io.Writef').
?- pj_add_predicate(writef/2, 'org.projog.core.predicate.builtin.io.Writef').

% kb (knowledge base)
?- pj_add_predicate(pj_add_arithmetic_operator/2, 'org.projog.core.predicate.builtin.kb.AddArithmeticOperator').
?- pj_add_predicate(arithmetic_function/1, 'org.projog.core.predicate.builtin.kb.AddUserDefinedArithmeticOperator').
?- pj_add_predicate(asserta/1, 'org.projog.core.predicate.builtin.kb.Assert/assertA').
?- pj_add_predicate(assertz/1, 'org.projog.core.predicate.builtin.kb.Assert/assertZ').
?- pj_add_predicate(assert/1, 'org.projog.core.predicate.builtin.kb.Assert/assertZ').
?- pj_add_predicate(listing/1, 'org.projog.core.predicate.builtin.kb.Listing').
?- pj_add_predicate(clause/2, 'org.projog.core.predicate.builtin.kb.Inspect/inspectClause').
?- pj_add_predicate(retract/1, 'org.projog.core.predicate.builtin.kb.Inspect/retract').
?- pj_add_predicate(retractall/1, 'org.projog.core.predicate.builtin.kb.RetractAll').
?- pj_add_predicate(consult/1, 'org.projog.core.predicate.builtin.kb.Consult').
?- pj_add_predicate('.'/2, 'org.projog.core.predicate.builtin.kb.ConsultList').
?- pj_add_predicate(ensure_loaded/1, 'org.projog.core.predicate.builtin.kb.EnsureLoaded').
?- pj_add_predicate(flag/3, 'org.projog.core.predicate.builtin.kb.Flag').
?- pj_add_predicate(current_predicate/1, 'org.projog.core.predicate.builtin.kb.CurrentPredicate').
?- pj_add_predicate('/'('dynamic', 1), 'org.projog.core.predicate.builtin.kb.Dynamic').

% db (recorded database)
?- pj_add_predicate(erase/1, 'org.projog.core.predicate.builtin.db.Erase').
?- pj_add_predicate(recorded/2, 'org.projog.core.predicate.builtin.db.Recorded').
?- pj_add_predicate(recorded/3, 'org.projog.core.predicate.builtin.db.Recorded').
?- pj_add_predicate(recorda/2, 'org.projog.core.predicate.builtin.db.InsertRecord/recordA').
?- pj_add_predicate(recorda/3, 'org.projog.core.predicate.builtin.db.InsertRecord/recordA').
?- pj_add_predicate(recordz/2, 'org.projog.core.predicate.builtin.db.InsertRecord/recordZ').
?- pj_add_predicate(recordz/3, 'org.projog.core.predicate.builtin.db.InsertRecord/recordZ').

% flow control
?- pj_add_predicate(repeat/0, 'org.projog.core.predicate.builtin.flow.RepeatInfinitely').
?- pj_add_predicate(repeat/1, 'org.projog.core.predicate.builtin.flow.RepeatSetAmount').
?- pj_add_predicate('!'/0, 'org.projog.core.predicate.builtin.flow.Cut').
?- pj_add_predicate(throw/1, 'org.projog.core.predicate.builtin.flow.Throw').

% list
?- pj_add_predicate(length/2, 'org.projog.core.predicate.builtin.list.Length').
?- pj_add_predicate(reverse/2, 'org.projog.core.predicate.builtin.list.Reverse').
?- pj_add_predicate(member/2, 'org.projog.core.predicate.builtin.list.Member').
?- pj_add_predicate(memberchk/2, 'org.projog.core.predicate.builtin.list.MemberCheck').
?- pj_add_predicate(min_list/2, 'org.projog.core.predicate.builtin.list.ExtremumList/minList').
?- pj_add_predicate(max_list/2, 'org.projog.core.predicate.builtin.list.ExtremumList/maxList').
?- pj_add_predicate(append/3, 'org.projog.core.predicate.builtin.list.Append').
?- pj_add_predicate(append/2, 'org.projog.core.predicate.builtin.list.AppendListOfLists').
?- pj_add_predicate(subtract/3, 'org.projog.core.predicate.builtin.list.SubtractFromList').
?- pj_add_predicate(keysort/2, 'org.projog.core.predicate.builtin.list.KeySort').
?- pj_add_predicate(flatten/2, 'org.projog.core.predicate.builtin.list.Flatten').
?- pj_add_predicate(sort/2, 'org.projog.core.predicate.builtin.list.SortAsSet').
?- pj_add_predicate(msort/2, 'org.projog.core.predicate.builtin.list.Sort').
?- pj_add_predicate(delete/3, 'org.projog.core.predicate.builtin.list.Delete').
?- pj_add_predicate(subset/2, 'org.projog.core.predicate.builtin.list.Subset').
?- pj_add_predicate(select/3, 'org.projog.core.predicate.builtin.list.Select').
?- pj_add_predicate(nth0/3, 'org.projog.core.predicate.builtin.list.Nth/nth0').
?- pj_add_predicate(nth1/3, 'org.projog.core.predicate.builtin.list.Nth/nth1').
?- pj_add_predicate(nth/3, 'org.projog.core.predicate.builtin.list.Nth/nth1').
?- pj_add_predicate(maplist/2, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(maplist/3, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(maplist/4, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(maplist/5, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(maplist/6, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(maplist/7, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(maplist/8, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(maplist/9, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(maplist/10, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(checklist/2, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(checklist/3, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(checklist/4, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(checklist/5, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(checklist/6, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(checklist/7, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(checklist/8, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(checklist/9, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(checklist/10, 'org.projog.core.predicate.builtin.list.MapList').
?- pj_add_predicate(include/3, 'org.projog.core.predicate.builtin.list.SubList').
?- pj_add_predicate(sublist/3, 'org.projog.core.predicate.builtin.list.SubList').
?- pj_add_predicate(foldl/4, 'org.projog.core.predicate.builtin.list.Fold').
?- pj_add_predicate(last/2, 'org.projog.core.predicate.builtin.list.Last').
?- pj_add_predicate(atomic_list_concat/2, 'org.projog.core.predicate.builtin.list.AtomicListConcat').
?- pj_add_predicate(atomic_list_concat/3, 'org.projog.core.predicate.builtin.list.AtomicListConcat').
?- pj_add_predicate(pairs_keys/2, 'org.projog.core.predicate.builtin.list.PairsElements/keys').
?- pj_add_predicate(pairs_values/2, 'org.projog.core.predicate.builtin.list.PairsElements/values').

% clp
?- pj_add_predicate(in/2, 'org.projog.core.predicate.builtin.clp.In').
?- pj_add_predicate(ins/2, 'org.projog.core.predicate.builtin.clp.In').
?- pj_add_predicate(label/1, 'org.projog.core.predicate.builtin.clp.Resolve').
?- pj_add_predicate(all_different/1, 'org.projog.core.predicate.builtin.clp.Distinct').
?- pj_add_predicate(all_distinct/1, 'org.projog.core.predicate.builtin.clp.Distinct').
?- pj_add_predicate('#<'/2, 'org.projog.core.predicate.builtin.clp.NumericConstraintPredicate/lessThan').
?- pj_add_predicate('#>'/2, 'org.projog.core.predicate.builtin.clp.NumericConstraintPredicate/greaterThan').
?- pj_add_predicate('#=<'/2, 'org.projog.core.predicate.builtin.clp.NumericConstraintPredicate/lessThanOrEqualTo').
?- pj_add_predicate('#>='/2, 'org.projog.core.predicate.builtin.clp.NumericConstraintPredicate/greaterThanOrEqualTo').
?- pj_add_predicate('#='/2, 'org.projog.core.predicate.builtin.clp.NumericConstraintPredicate/equalTo').
?- pj_add_predicate('#\\='/2, 'org.projog.core.predicate.builtin.clp.NumericConstraintPredicate/notEqualTo').
?- pj_add_predicate('#<==>'/2, 'org.projog.core.predicate.builtin.clp.BooleanConstraintPredicate/equivalent').
?- pj_add_predicate('#==>'/2, 'org.projog.core.predicate.builtin.clp.BooleanConstraintPredicate/leftImpliesRight').
?- pj_add_predicate('#<=='/2, 'org.projog.core.predicate.builtin.clp.BooleanConstraintPredicate/rightImpliesLeft').
?- pj_add_predicate('#/\\'/2, 'org.projog.core.predicate.builtin.clp.BooleanConstraintPredicate/and').
?- pj_add_predicate('#\\/'/2, 'org.projog.core.predicate.builtin.clp.BooleanConstraintPredicate/or').
?- pj_add_predicate('/'('#\\', 2), 'org.projog.core.predicate.builtin.clp.BooleanConstraintPredicate/xor').
?- pj_add_predicate('/'('#\\', 1), 'org.projog.core.predicate.builtin.clp.BooleanConstraintPredicate/not').
?- pj_add_predicate(pj_add_clp_expression/2, 'org.projog.core.predicate.builtin.clp.AddExpressionFactory').
?- pj_add_clp_expression('+'/2, 'org.projog.core.predicate.builtin.clp.CommonExpression/add').
?- pj_add_clp_expression('/'('-', 2), 'org.projog.core.predicate.builtin.clp.CommonExpression/subtract').
?- pj_add_clp_expression('*'/2, 'org.projog.core.predicate.builtin.clp.CommonExpression/multiply').
?- pj_add_clp_expression('//'/2, 'org.projog.core.predicate.builtin.clp.CommonExpression/divide').
?- pj_add_clp_expression('min'/2, 'org.projog.core.predicate.builtin.clp.CommonExpression/minimum').
?- pj_add_clp_expression('max'/2, 'org.projog.core.predicate.builtin.clp.CommonExpression/maximum').
?- pj_add_clp_expression('abs'/1, 'org.projog.core.predicate.builtin.clp.CommonExpression/absolute').
?- pj_add_clp_expression('/'('-', 1), 'org.projog.core.predicate.builtin.clp.CommonExpression/minus').

% time
?- pj_add_predicate(get_time/1, 'org.projog.core.predicate.builtin.time.GetTime').
?- pj_add_predicate(convert_time/2, 'org.projog.core.predicate.builtin.time.ConvertTime').

?- pj_add_predicate(dif/2, 'org.projog.core.predicate.builtin.reif.Dif').

% numerical operations
?- pj_add_arithmetic_operator('+'/2, 'org.projog.core.math.builtin.Add').
?- pj_add_arithmetic_operator('/'('-', 1), 'org.projog.core.math.builtin.Minus').
?- pj_add_arithmetic_operator('/'('-', 2), 'org.projog.core.math.builtin.Subtract').
?- pj_add_arithmetic_operator('/'/2, 'org.projog.core.math.builtin.Divide').
?- pj_add_arithmetic_operator('//'/2, 'org.projog.core.math.builtin.IntegerDivide').
?- pj_add_arithmetic_operator('*'/2, 'org.projog.core.math.builtin.Multiply').
?- pj_add_arithmetic_operator('**'/2, 'org.projog.core.math.builtin.Power').
?- pj_add_arithmetic_operator('^'/2, 'org.projog.core.math.builtin.Power').
?- pj_add_arithmetic_operator(mod/2, 'org.projog.core.math.builtin.Modulo').
?- pj_add_arithmetic_operator(rem/2, 'org.projog.core.math.builtin.Remainder').
?- pj_add_arithmetic_operator(random/1, 'org.projog.core.math.builtin.Random').
?- pj_add_arithmetic_operator(integer/1, 'org.projog.core.math.builtin.Round').
?- pj_add_arithmetic_operator('/\\'/2, 'org.projog.core.math.builtin.BitwiseAnd').
?- pj_add_arithmetic_operator('\\/'/2, 'org.projog.core.math.builtin.BitwiseOr').
?- pj_add_arithmetic_operator(xor/2, 'org.projog.core.math.builtin.BitwiseXor').
?- pj_add_arithmetic_operator('<<'/2, 'org.projog.core.math.builtin.ShiftLeft').
?- pj_add_arithmetic_operator('>>'/2, 'org.projog.core.math.builtin.ShiftRight').
?- pj_add_arithmetic_operator(max/2, 'org.projog.core.math.builtin.Max').
?- pj_add_arithmetic_operator(min/2, 'org.projog.core.math.builtin.Min').
?- pj_add_arithmetic_operator(abs/1, 'org.projog.core.math.builtin.Abs').

% definite clause grammers (DCG)
?- op(1200, xfx, '-->').
?- op(901, fx, '{').
?- op(900, xf, '}').

% operators
?- op(1200, xfx, ':-').
?- op(1200, fx, ':-').
?- op(1100, fx, dynamic).
?- op(1100, xfy, ';').
?- op(1050, xfy, '->').
?- op(1000, xfy, ',').
?- op(900, fy, '\\+').
?- op(700, xfx, '=').
?- op(700, xfx, '==').
?- op(700, xfx, '=:=').
?- op(700, xfx, '=\\=').
?- op(700, xfx, '=..').
?- op(700, xfx, '<').
?- op(700, xfx, '>').
?- op(700, xfx, '=<').
?- op(700, xfx, '>=').
?- op(700, xfx, '@<').
?- op(700, xfx, '@=<').
?- op(700, xfx, '@>').
?- op(700, xfx, '@>=').
?- op(700, xfx, '\\=').
?- op(700, xfx, '\\==').
?- op(700, xfx, is).
?- op(700, xfx, in).
?- op(700, xfx, ins).
?- op(700, xfx, '#=').
?- op(700, xfx, '#\\=').
?- op(700, xfx, '#<').
?- op(700, xfx, '#>').
?- op(700, xfx, '#=<').
?- op(700, xfx, '#>=').
?- op(760, yfx, '#<==>').
?- op(750, xfy, '#==>').
?- op(750, xfy, '#<==').
?- op(720, yfx, '#/\\').
?- op(740, yfx, '#\\/').
?- op(730, xfy, '#\\').
?- op(710, fy, '#\\').
?- op(600, xfy, '..').
?- op(600, xfy, ':').
?- op(500, yfx, '+').
?- op(500, yfx, '-').
?- op(400, yfx, '*').
?- op(400, yfx, '**').
?- op(400, yfx, '^').
?- op(400, yfx, '//').
?- op(400, yfx, mod).
?- op(400, yfx, rem).
?- op(400, yfx, '/\\').
?- op(400, yfx, '\\/').
?- op(400, yfx, xor).
?- op(400, yfx, '<<').
?- op(400, yfx, '>>').
?- op(200, fy, '-').
