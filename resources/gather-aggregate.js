fromCategory('MyAggregate')
    .foreachStream().when(
        {
            $any : function(s,e) {
                linkTo("MyAggregate", e);
            }
        })