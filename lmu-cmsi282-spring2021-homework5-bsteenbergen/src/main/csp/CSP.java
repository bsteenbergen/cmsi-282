package main.csp;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some set of unary and binary 
 * constraints on the dates of each meeting.
 */
public class CSP {

    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
        List<DateVar> meetings = new ArrayList<>();
        for (int i = 0; i < nMeetings; i++) {
            DateVar meeting = new DateVar(rangeStart, rangeEnd);
            meetings.add(meeting);
        }
        List<LocalDate> assignments = new ArrayList<>();
        nodePreprocessing(meetings, constraints);
        AC3(meetings, constraints);
        return backtrack(nMeetings, meetings, assignments, constraints);
    }
    
    /**
     * Recursive method to perform backtracking for all meetings and create the assignment of dates.
     * @param nMeetings The number of meetings that must be scheduled
     * @param meetings A list of meetings that each hold their own domain
     * @param assignments The list of dates that satisfy the constraints
     * @param constraints The set of constraints the solution must satisfy
     * @return A list of LocalDates that satisfy the constraints, or null if the constraints cannot be
     *         satisfied.
     */
    private static List<LocalDate> backtrack (int nMeetings, List<DateVar> meetings, List<LocalDate> assignments, 
            Set<DateConstraint> constraints) {
        if (nMeetings == assignments.size()) {
            return assignments;
        }
        
        DateVar current = meetings.get(assignments.size());
        List<LocalDate> result = new ArrayList<>();
        for (LocalDate day : current.domain) {
            assignments.add(day);
            if (isConsistent(assignments, constraints)) {
                result = backtrack(nMeetings, meetings, assignments, constraints);
                if (result != null) {
                    return result;
                }
            }
            assignments.remove(assignments.size() - 1);
        }
        return null;
    }
    
    /**
     * A method to assist in backtracking that checks the consistency of the given set of 
     *  DateConstraints with the solution.
     * @param soln The assignments to check
     * @param constraints The set of constraints the solution must satisfy
     * @return True if the solution is consistent, false otherwise.
     */
    private static Boolean isConsistent (List<LocalDate> soln, Set<DateConstraint> constraints) {
        for (DateConstraint d : constraints) {
            if (d.arity() == 2) {
                BinaryDateConstraint constraint = (BinaryDateConstraint) d;
                int left = d.L_VAL;
                int right = constraint.R_VAL;
                if (left < soln.size() && right < soln.size()) {
                    LocalDate leftDate = soln.get(left);
                    LocalDate rightDate = soln.get(right);
                    if (!checkDates(leftDate, rightDate, d)) {
                        return false;
                    }
                }            
            }
            else {
                UnaryDateConstraint constraint = (UnaryDateConstraint) d;
                int left = d.L_VAL;
                LocalDate rightDate = constraint.R_VAL;
                if (left < soln.size()) {
                    LocalDate leftDate = soln.get(left);
                    if (!checkDates(leftDate, rightDate, d)) {
                        return false;
                    }
                }
            }
        }      
        return true;
    }
    
    /**
     * A method to assist in pruning unnecessary dates from the meetings' domains based on given
     * UnaryDateConstraints.
     * @param meetings The meetings to edit the domains of
     * @param contraints The set of constraints the solution must satisfy
     */
    private static void nodePreprocessing (List<DateVar> meetings, Set<DateConstraint> constraints) {
        List<LocalDate> toRemove = new ArrayList<>();
        for (DateVar m : meetings) {
            for (DateConstraint d : constraints) {
                if (d.arity() == 1) {
                    UnaryDateConstraint constraint = (UnaryDateConstraint) d;
                    LocalDate right = constraint.R_VAL;
                    for (LocalDate day : m.domain) {
                        if (!checkDates(day, right, constraint)) {
                            toRemove.add(day);
                        }
                    }
                }
            }
            m.domain.removeAll(toRemove);
        }
    }
    
    /**
     * Creates a list of arcs from the given set of constraints.
     * @param constraints The set of constraints the solution must satisfy
     * @return A Queue of arcs to be used in AC3.
     */
    private static Queue<Arc> makeArcs (Set<DateConstraint> constraints) {
        Queue<Arc> arcs = new LinkedList<>();
        for (DateConstraint d : constraints) {
            if (d.arity() == 2) {
                BinaryDateConstraint constraint = (BinaryDateConstraint) d;
                Arc newArc = new Arc(constraint.L_VAL, constraint.R_VAL);
                Arc newArc2 = new Arc(constraint.R_VAL, constraint.L_VAL);
                
                if (!arcs.contains(newArc)) {
                    arcs.add(newArc);
                }
                if (!arcs.contains(newArc2)) {
                    arcs.add(newArc2);
                }
            }
        }
        return arcs;
    }
    
    /**
     * Performs the pruning of unnecessary dates in the domains of the meetings for 
     *  BinaryDateConstraints.
     * @param meetings List of meetings that each have a domain of LocalDates
     * @param constraints The set of constraints the solution must satisfy
     */
    private static void AC3 (List<DateVar> meetings, Set<DateConstraint> constraints) {
        Queue<Arc> arcs = makeArcs(constraints);
        while (!arcs.isEmpty()) {
            Arc removed = arcs.remove();
            List<LocalDate> toRemove = removeInconsistent(meetings, constraints, removed);
            if (!toRemove.isEmpty()) {
                meetings.get(removed.head).domain.removeAll(toRemove);
                for (Arc toAdd : findNeighbors(removed, constraints)) {
                    arcs.add(toAdd);
                }
            }
        }
    }
    
    /**
     * Helper for AC3 that creates a list of meetings to be removed.
     * @param meetings The meetings that contain the domains to be checked
     * @param constraints The set of constraints the solution must satisfy
     * @param toCheck The arc of which to be checked for consistency
     * @return A list of LocalDates to be removed from the domain of the arc.
     */
    private static List<LocalDate> removeInconsistent (List<DateVar> meetings, Set<DateConstraint> constraints, Arc toCheck) {
        Boolean removed = false;
        List<LocalDate> toRemove = new ArrayList<>();
        for (DateConstraint d : constraints) {
            if (d.arity() == 2) {
                BinaryDateConstraint constraint = (BinaryDateConstraint) d;
                
                for (LocalDate dayInHead : meetings.get(toCheck.head).domain) {
                    for (LocalDate dayInTail : meetings.get(toCheck.tail).domain) {
                        if (checkDates(dayInHead, dayInTail, constraint)) {
                            removed = true;
                        }
                        if (checkDates(dayInTail, dayInHead, constraint)) {
                            removed = true;
                        }
                    }
                    if (!removed) {
                        toRemove.add(dayInHead);
                    }
                }
            }
        }
        return toRemove;
    }
    
    /**
     * A method to assist in AC3 which creates the neighbors of the given arc to be added into the
     * Queue.
     * @param toCheck The arc of which to find the neighbors
     * @param constraints The set of constraints the solution must satisfy
     * @return A Queue of arcs to be added to the main queue.
     */
    private static Queue<Arc> findNeighbors (Arc toCheck, Set<DateConstraint> constraints) {
        Queue<Arc> neighbors = new LinkedList<>();
        for (DateConstraint d : constraints) {
            if (d.arity() == 2) {
                BinaryDateConstraint constraint = (BinaryDateConstraint) d;
                if (constraint.L_VAL == toCheck.tail) {
                    neighbors.add(new Arc(constraint.R_VAL, toCheck.tail));
                }
                if (constraint.R_VAL == toCheck.tail) {
                    neighbors.add(new Arc(constraint.L_VAL, toCheck.tail));
                }
            }
        }
        return neighbors;
    }
    
    /**
     * A method (given kindly by Dr. Forney) that checks the given dates and whether or not they are
     * consistent.
     * @param leftDate The left date to be checked
     * @param rightDate The right date to be checked
     * @param constraint The constraint the left and right date must satisfy
     * @return True if the constraint is satisfied, false otherwise.
     */
    private static Boolean checkDates (LocalDate leftDate, LocalDate rightDate, DateConstraint constraint) {
        if (leftDate == null || rightDate == null) {
            return true;
        }
        boolean sat = false;
        switch (constraint.OP) {
        case "==": if (leftDate.isEqual(rightDate))  sat = true; break;
        case "!=": if (!leftDate.isEqual(rightDate)) sat = true; break;
        case ">":  if (leftDate.isAfter(rightDate))  sat = true; break;
        case "<":  if (leftDate.isBefore(rightDate)) sat = true; break;
        case ">=": if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate))  sat = true; break;
        case "<=": if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) sat = true; break;
        }
        return sat;
    }
    
    /**
     * A private class to keep track of the domain for each meeting.
     *
     */
    private static class DateVar {
        List<LocalDate> domain;
        
        /**
         * Constructs a new date variable for the meeting.
         * @param start The LocalDate that starts the range of dates
         * @param end The LocalDate that ends the range of dates
         */
        DateVar (LocalDate start, LocalDate end) {
            this.domain = makeDomain(start, end);        
        }
        
        /**
         * A helper that creates the domain of allowable dates for each meeting.
         * @param start The LocalDate that starts the range of dates
         * @param end The LocalDate that ends the range of dates
         * @return A list of LocalDates for the meeting to be scheuled on.
         */
        private List<LocalDate> makeDomain (LocalDate start, LocalDate end) {
            return start.datesUntil(end.plusDays(1)).collect(Collectors.toList());            
        }
    }
    
    /**
     * A private class to generate arcs of related variables
     *
     */
    private static class Arc {
        int head;
        int tail;
        
        /**
         * Constructs a new arc with the given head and tail.
         * @param head The meeting index of the head
         * @param tail The meeting index of the tail
         */
        Arc (int head, int tail) {
            this.head = head;
            this.tail = tail;
        }
    }
}
