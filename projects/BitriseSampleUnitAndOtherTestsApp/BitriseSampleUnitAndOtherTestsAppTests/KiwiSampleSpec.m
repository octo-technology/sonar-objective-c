//
//  KiwiSampleSpec.m
//  BitriseSampleUnitAndOtherTestsApp
//
//  Created by Viktor Benei on 3/3/15.
//  Copyright (c) 2015 Bitrise. All rights reserved.
//

#import "Kiwi.h"

SPEC_BEGIN(KiwiSampleSpec)

describe(@"KiwiSample", ^{
    it(@"is pretty cool", ^{
        NSUInteger a = 16;
        NSUInteger b = 26;
        [[theValue(a + b) should] equal:theValue(42)];
    });
});

SPEC_END