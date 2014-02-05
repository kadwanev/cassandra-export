cassandra-export
================

Distributed export of cassandra sstable data to JSON on hadoop.

Currently exists as a kernel which can take sstables and start cassandra itself to export as JSON. This enables compatibility with every version of cassandra and future-proofing.
When this is fully automated, it would be able to convert a massive cassandra dataset in a matter minutes.

This would now be an alternative to the recently open sourced Netflix Aegisthus project.


*TODO*

Remove Scalding attempts and more forward with SHadoop

