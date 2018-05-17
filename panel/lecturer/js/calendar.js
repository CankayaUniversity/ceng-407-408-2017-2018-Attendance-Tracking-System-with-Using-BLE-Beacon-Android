 $(document).ready(function() {

    $('#calendar').fullCalendar({
      defaultDate: '2018-05-01',
      events: [
        {
          title: 'CENG 408',
          start: '2018-05-02'
        },
        {
          title: 'CENG 408',
          start: '2018-05-03'
        },
        {
          title: 'Home',
          url: 'http://attendancesystem.xyz',
          start: '2018-05-15'
        }
      ]
    });

  });