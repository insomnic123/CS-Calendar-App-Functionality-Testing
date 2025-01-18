// This file is merely a rewrite with new perspective to see if I can make it more functional

public class ProcessingTryTwo {

    /*
    Steps:
    1. Make a function which returns the number of days free leading up to the due date
    2. Make a function which assigns how much work should be done each day (proportional allocation) and returns an
    arraylist (the index can be tracked through iteration)
    3. For each day, create an arraylist of arraylist pairs containing the timeslots within that day which are free and
    longer than 30 mins ; ex. {{LocalDateTime, LocalDateTime}, {LocalDateTime, LocalDateTime}}
    4. Calculate the duration of each starttime/endtime pair, and create nonnegotiable events with the starttime/endtime
    pairs such that you get a HashMap that is <Duration, NonNegotiable> -- then create a new treemap which has the
    data sorted by duration from longest to shortest timespan
    5. Create new events for the assignment such if the amount needed to work that day exceeds the duration of the
    current timespan, the timespan is filled and the next timeslot is checked for the same thing, until the remaining
    time to do the work is 0
    6. Add the events to a schedule and return it accordingly
     */

}
