function echo(txt) {
    return txt
}

Assert.assertTrue(echo('a')=='a');
Assert.assertTrue(echo('a')=='a','echo function is a parrot');
Assert.assertTrue(echo('a')=='a');

Assert.assertTrue(echo('a')=='b', 'Stupid assertion');