select * from employees where id = 3;
update employees set first_name = 'Alex' where id = 2;
delete from employees where id = 1;



select sum(salary), department from employees group by department;
