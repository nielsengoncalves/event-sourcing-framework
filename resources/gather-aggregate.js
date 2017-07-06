fromCategory('MyAggregateRoot')
    .foreachStream().when(
        {
            $any : function(s,e) {
                linkTo("MyAggregateRoot", e);
            }
        })