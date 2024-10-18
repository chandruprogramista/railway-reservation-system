package com.chand.railway_reservation_system;

import com.chand.railway_reservation_system.core.datastructure.Tree;
import com.chand.railway_reservation_system.core.entity.Passenger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
//		SpringApplication.run(Application.class, args);
		Collection<Integer> t = new Tree<>();
		int[] arr = {7, 3, 20, 1, 5};

		for (int i : arr)
			t.add(i);

		System.out.println(t);

		t.add(6);

		System.out.println(t);
		t.add(21);
		t.add(22);

		System.out.println(t);
		System.out.println(Tree.preAddCheck(t, 0));
		System.out.println(Tree.preAddCheck(t, 4));

		System.out.println(t.remove(6));

		System.out.println(t);

		Collection<Passenger> p = new Tree<>();

		int[][] arrr = {
				{9, 12},
				{1, 3},
				{5, 9},
				{13, 20},
				{21, 23}
		};

		for (int i = 0; i < arrr.length; i++) {
			System.out.println(p.add(new Passenger("chand - " + i, "" + (('A' + arrr[i][0]) - 1), "" + (('A' + arrr[i][1]) - 1), 2)));
		}

		System.out.println(p);

		// (obj1, obj2) -> obj1.snd[1] <= obj2.snd[0] ? -1 : obj1.snd[0] >= obj2.snd[1] ? 1 : 0
//		Collection<Dummy> d = new Tree<>();
//
//		int[][] arrr = {
//				{9, 12},
//				{1, 3},
//				{5, 9},
//				{13, 20},
//				{21, 23}
//		};
//
//		for (int  i = 0; i < arrr.length; i++) {
//			System.out.println(d.add(new Dummy(arrr[i], i)));
//		}
//
//		System.out.println(d);
//
//		System.out.println(d.add(new Dummy(new int[] {3, 5}, 5)));
////		System.out.println(d.add(new Dummy(new int[] {17, 20}, 6)));
////		System.out.println(d.add(new Dummy(new int[] {3, 4}, 7)));
//
//		System.out.println(d.add(new Dummy(new int[] {10, 13}, 7)));
//		System.out.println(d.add(new Dummy(new int[] {14, 17}, 7)));
//
//
//		System.out.println(d);
	}
}

//class Dummy implements Comparable<Dummy> {
//	int[] snd;
//	int unique;
//
//	public Dummy(int[] snd, int unique) {
//		this.snd = snd;
//		this.unique = unique;
//	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (this == o) return true;
//		if (o == null || getClass() != o.getClass()) return false;
//		Dummy dummy = (Dummy) o;
//		return unique == dummy.unique && Objects.deepEquals(snd, dummy.snd);
//	}
//
//	@Override
//	public String toString() {
//		return "Dummy{" +
//				"snd=" + Arrays.toString(snd) +
//				", unique=" + unique +
//				'}';
//	}
//
//	@Override
//	public int compareTo(Dummy that) {
//		return this.snd[1] <= that.snd[0] ? -1 : this.snd[0] >= that.snd[1] ? 1 : 0;
//	}
//}
