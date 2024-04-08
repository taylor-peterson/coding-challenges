import subprocess

TEST_FILE = "test.txt"
test_cases = [
    f"<command> -c {TEST_FILE}",
    f"<command> -l {TEST_FILE}",
    f"<command> -w {TEST_FILE}",
    f"<command> -m {TEST_FILE}",
    f"<command>    {TEST_FILE}",
    f"cat {TEST_FILE} | <command> -l",
]

for test_case in test_cases:
    wc = subprocess.check_output(
        test_case.replace("<command>", "wc"), shell=True
    ).split()
    ccwc = subprocess.check_output(
        test_case.replace("<command>", "./ccwc"), shell=True
    ).split()
    assert wc == ccwc
