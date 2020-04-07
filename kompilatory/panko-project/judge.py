#!/usr/bin/python

import sys
import re
import os
import sh
import argparse
from collections import defaultdict

def load_programs(project):
    r = dict();
    with open(project + '/inputs.in') as f:
        for line in f:
            pair = re.split("[ \t]+", line.strip());
            if len(pair) != 2:
                continue
            r[pair[0]] = project + '/' + pair[1]
    return r



if __name__ == '__main__':
    args_p = argparse.ArgumentParser('Testovac kompilatorov')
    args_p.add_argument('project', default='c++', nargs='?',
                        help='Ktory projekt otestovat.')
    args_p.add_argument('programs', type=str, nargs='*',
                        help='Ktore programy spustit.',
                        default=['primes', 'sort', 'graph'])
    args = args_p.parse_args()
    project = args.project
    programs = args.programs 
    if len(sys.argv) >= 2:
        project = sys.argv[1]
        if len(sys.argv) > 2:
            programs = sys.argv[2:]
    files = load_programs(project)
    ret = os.system(project + '/build.sh')
    if ret != 0:
        print("build ends with non-zero status");
    prog_stat = defaultdict(lambda:defaultdict(int))
    prog_bad = defaultdict(list)
    for program in programs:
        # compile program
        binary = str(sh.tempfile()).strip()
        ret = os.system(project + '/compile.sh ' + files[program] + ' ' + binary)
        if ret != 0:
            print("Unable to compile!")
        # get testsi
        
        path = 'test/' + program + '/in'
        tests = [ 
            f.strip() 
            for f in os.listdir(path) 
            if os.path.isfile(os.path.join(path, f)) 
        ]
        # run tests
        tempfile = sh.tempfile().strip()
        for test in tests:
            run = './{project}/run.sh'.format(
                project=project,
            )
            sh.Command(run)(
                binary,
                _in = open('./test/{program}/in/{test}'.format(
                    program=program,
                    test=test,
                )),
                _out = tempfile,
            )
            out_correct = './test/{program}/out/{test}'.format(
                program=program,
                test=test,
            )
            same = True
            try:
                ret = sh.diff(tempfile, out_correct, _ok_code=range(0,2))
                if ret.exit_code == 1:
                    print('Tvoj program dal zly vysledok v ulohe {} na priklade {}:\n{}'.format(program, test, ret))
                    same = False
                    prog_bad[program].append(test)
            except sh.ErrorReturnCode:
                print('UGH: toto by sa nemalo stat.')
                exit(1)
            prog_stat[program][same] += 1
    for program in programs:
        print('Program {}: {}/{}, chybne vysledky na vstupoch: {}'.format(
            program,
            prog_stat[program][True],
            prog_stat[program][True] + prog_stat[program][False],
            prog_bad[program],
        ))
