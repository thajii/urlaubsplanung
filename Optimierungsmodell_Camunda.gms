Set project / p1*p3 /
    employee / e1*e5 /
    project_member(employee, project) /
    e1 .p1, e1 .p3,
    e2 .p1, e2 .p2,
    e3 .p1, e3 .p3,
    e4 .p2, e4 .p3,
    e5 .p1, e5 .p2 /
    day / d1*d12 /;
            
Table prio_list(employee, day)
    d1  d2  d3  d4  d5  d6  d7  d8  d9  d10 d11 d12
e1  0   2   3   3   3   2   2   0   0   0   1   1
e2  1   1   1   1   0   0   0   0   0   1   1   1
e3  0   1   3   1   1   1   0   0   0   1   0   0
e4  0   1   3   3   2   1   0   0   0   1   0   0
e5  0   1   3   3   2   0   0   1   1   1   1   1;

Parameter
num_days_off_min(employee) / e1 2, e2 2, e3 2, e4 2, e5 3 /
num_days_off_max(employee) / e1 2, e2 2, e3 2, e4 2, e5 3 /
earliest_day_off(employee) / e1 2, e2 1, e3 1, e4 1, e5 1 /
latest_day_off(employee) / e1 8, e2 12, e3 12, e4 12, e5 12 /
num_project_members(project) / p1 4, p2 3, p3 3 / 
p(project , day) 'binary matrix';
        
Variable 
    z 'Zielfunktion'
    x(employee, day) 'binäre Urlaubstabelle';
    
Binary variable
    x;
    
Equation
    eq_gesamtzufriedenheit 'Zufriedenheit gemessen in Priosumme'
    eq_min_days_off 'Mindestanzahl an Urlaub pro Mitarbeiter'
    eq_max_days_off 'Maximalanzahl an Urlaub pro Mitarbeiter'
    eq_earliest_day_off 'Frühstens ab da Urlaub'
    eq_latest_day_off 'Spätestens ab da Urlaub'
    eq_num_project_members(project, day) 'Mindestens immer ein Mitarbeiter pro Projekt pro Tag';
    
eq_gesamtzufriedenheit.. z =e= sum(employee, sum(day, x(employee, day)*prio_list(employee, day)));
eq_min_days_off(employee).. sum(day, x(employee, day)) =G= num_days_off_min(employee);
eq_max_days_off(employee).. sum(day, x(employee, day)) =L= num_days_off_max(employee);
eq_earliest_day_off(employee).. sum(day, x(employee, day) $ (ord(day) < earliest_day_off(employee))) =e= 0;
eq_latest_day_off(employee).. sum(day, x(employee, day) $ (ord(day) > latest_day_off(employee))) =e= 0;
eq_num_project_members(project,day).. num_project_members(project) - sum(employee, x(employee, day) * project_member(employee, project)) =g= 1;


Model vacation /all/;
Solve vacation using  mip maximizing z;
display  z.l, x.l;

