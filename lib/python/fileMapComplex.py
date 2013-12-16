#!/usr/bin/python
# coding=utf-8

from common import *

if len(argv) != 6:
    print >> stderr, "Usage: %s main.Class dbDir ensured.main.Class storageVariant directoriesVariant" % argv[0]
    exit(1)

STUDENT_CLAZZ = argv[1]
DB_DIR = argv[2]
ENSURED_CLAZZ = argv[3]
STORAGE_VARIANT = argv[4]
DIRECTORIES_VARIANT = argv[5]

ENSURED_JAVA_OPTIONS = '-Dfizteh.db.dir=%s -Dfizteh.db.file.format=%s -Dfizteh.db.dir.format=%s %s' \
                       % (DB_DIR, STORAGE_VARIANT, DIRECTORIES_VARIANT, ENSURED_CLAZZ)
STUDENT_JAVA_OPTIONS = '-Dfizteh.db.dir=%s -Dfizteh.db.file.format=%s -Dfizteh.db.dir.format=%s %s' \
                       % (DB_DIR, STORAGE_VARIANT, DIRECTORIES_VARIANT, STUDENT_CLAZZ)


def create_map(size=10):
    map = dict()
    for i in range(size):
        key = 'key%d' % i
        map[key] = hash_value(key)
    map['ключ_на_русском_языке'] = 'значение_на_русском_языке'
    #map['пусто'] = ''
    return map

MAP = create_map()

def check_put_get():
    shell = ExpectWrapper(STUDENT_JAVA_OPTIONS)

    key1 = 'key1'
    value1 = hash_value(key1)
    key2 = 'key2'

    if DIRECTORIES_VARIANT != '0':
        shell.sendAndExpectAndGreet('create default', 'created')
        shell.sendAndExpectAndGreet('use default', 'using default')


    shell.sendAndExpectAndGreet('put %s %s' % (key1, value1), 'new')
    shell.sendAndExpectAndGreet('get %s' % key2, 'not found')
    shell.sendAndExpectAndGreet('get %s' % key1, 'found')
    shell.sendAndExpectAndGreet('put %s %s' % (key1, value1 + 'G'), ('overwrite', value1))
    shell.sendAndExpectAndGreet('remove %s' % key2, 'not found')
    shell.sendAndExpectAndGreet('remove %s' % key1, 'removed')
    shell.sendAndExpectAndGreet('get %s' % key1, 'not found')
    shell.sendExit()


def check_fill_in():
    shell = ExpectWrapper(STUDENT_JAVA_OPTIONS)

    if DIRECTORIES_VARIANT != '0':
        shell.sendAndExpectAndGreet('use default', 'using default')

    for key, value in MAP.items():
        shell.send('put %s %s' % (key, value))
        shell.expectGreet()
    
    shell.sendExit()


def check_persistence():
    shell = ExpectWrapper(STUDENT_JAVA_OPTIONS)

    if DIRECTORIES_VARIANT != '0':
        shell.sendAndExpectAndGreet('use default', 'using default')

    for key, value in MAP.items():
        shell.sendAndExpectAndGreet('get %s' % key, ('found', value))

        shell.sendAndExpectAndGreet('get %s' % value, 'not found')

    shell.sendExit()


def check_format_compatibility():
    ensured_shell = ExpectWrapper(ENSURED_JAVA_OPTIONS)

    if DIRECTORIES_VARIANT != '0':
        ensured_shell.sendAndExpectAndGreet('use default', 'using default')

    for key, value in MAP.items():
        ensured_shell.sendAndExpectAndGreet('get %s' % key, ('found', value))

    ensured_shell.sendExit()
    

def check_remove():
    shell = ExpectWrapper(STUDENT_JAVA_OPTIONS)

    if DIRECTORIES_VARIANT != '0':
        shell.sendAndExpectAndGreet('use default', 'using default')

    for key, value in MAP.items()[:3]:
        shell.sendAndExpectAndGreet('remove %s' % key, 'removed')
        shell.sendAndExpectAndGreet('remove %s' % value, 'not found')

    shell.sendExit()

def check_remove_consistency():
    ensured_shell = ExpectWrapper(ENSURED_JAVA_OPTIONS)

    if DIRECTORIES_VARIANT != '0':
        ensured_shell.sendAndExpectAndGreet('use default', 'using default')

    for key, value in MAP.items()[:3]:
        ensured_shell.sendAndExpectAndGreet('get %s' % key, 'not found')

    for key, value in MAP.items()[3:]:
        ensured_shell.sendAndExpectAndGreet('get %s' % key, 'found', value)

    ensured_shell.sendExit()

# exec_test(check_put_get, 'Не работает базовая функциональность', True)
# exec_test(check_fill_in, 'Ошибка при наполнении базы', True)
# exec_test(check_persistence, 'Ошибка перзистентности', True)
# exec_test(check_format_compatibility, 'Несовместимый формат', True)
# exec_test(check_remove, 'Ошибка при очищении хранилища', True)
# exec_test(check_remove_consistency, 'Ошибка целостности после remove', True)
