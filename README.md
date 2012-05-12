# MMSEG

* A lucene Tokenizer write for [Rssminer](http://rssminer.net),
* Algorithm: [MMSEG](http://technology.chtsai.org/mmseg/)

## Goal

* Memory efficiency. Rssminer is running on a VPS with only 512M
  RAM. I do not have plan to buy a larger VPS, but do have plan to
  support thousands of concurrent user.

* Fast. Again, thousands of users with only one CPU with one core.

## Limitation

* Currenly only `Simple maximum matching` is implemented, but the
  `Complex` is on the schedule

* A Simple Hash set is used by store the Dictionary. which use 5-6M
  heap when 149853 word is loaded. For `Maximum Matching`, A faster
  [Double-Array Trie](http://linux.thai.net/~thep/datrie/datrie.html)
  is on the schedule.

