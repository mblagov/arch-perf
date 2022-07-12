select *
from (select employees.*,
             row_number() over (partition by id order by row_version desc) as rn
      from employees) as t
where rn = 1

